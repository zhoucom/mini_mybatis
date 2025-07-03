package com.example.mybatis.core;

import com.example.mybatis.cache.CacheConfig;
import com.example.mybatis.cache.CacheManager;
import com.example.mybatis.exception.SqlExecutionException;
import com.example.mybatis.security.SqlInjectionGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * 默认SQL会话实现 - MyBatis核心执行引擎
 * 
 * 这个类是MyBatis的核心执行器，实现了以下关键功能：
 * 1. SQL执行：支持增删改查操作
 * 2. 缓存管理：集成一级和二级缓存机制
 * 3. 安全防护：SQL注入检测和权限验证
 * 4. 事务管理：通过Spring事务注解支持
 * 5. 参数处理：智能参数解析和映射
 * 
 * 相比传统MyBatis的改进：
 * - 增强了安全性检查机制
 * - 优化了缓存策略
 * - 集成了Spring事务管理
 * - 添加了详细的日志记录
 */
@Transactional
public class DefaultSqlSession implements SqlSession {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);
    
    /** 配置对象 - 存储MyBatis全局配置信息 */
    private final MyBatisConfiguration configuration;
    
    /** JDBC模板 - Spring提供的数据库访问工具 */
    private final JdbcTemplate jdbcTemplate;
    
    /** 缓存管理器 - 负责一级和二级缓存的管理 */
    private final CacheManager cacheManager;
    
    /**
     * 构造函数 - 初始化SQL会话
     * 
     * 初始化过程：
     * 1. 验证数据源有效性
     * 2. 创建JdbcTemplate实例
     * 3. 初始化缓存管理器
     */
    public DefaultSqlSession(MyBatisConfiguration configuration) {
        this.configuration = configuration;
        DataSource dataSource = configuration.getDataSource();
        if (dataSource == null) {
            throw new SqlExecutionException("数据源不能为空");
        }
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        
        // 初始化缓存管理器 - 支持可配置的缓存策略
        CacheConfig cacheConfig = configuration.getCacheConfig();
        if (cacheConfig == null) {
            cacheConfig = new CacheConfig(); // 使用默认配置
        }
        this.cacheManager = new CacheManager(cacheConfig);
    }
    
    /**
     * 查询单个对象
     * 
     * 功能特点：
     * - 自动校验返回结果数量
     * - 复用selectList方法逻辑
     * - 提供清晰的错误提示
     */
    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = selectList(statement, parameter);
        if (list == null || list.isEmpty()) {
            return null;
        }
        // 防止数据不一致：确保单对象查询只返回一个结果
        if (list.size() > 1) {
            throw new RuntimeException("期望返回一个结果，但查询到了 " + list.size() + " 个结果");
        }
        return list.get(0);
    }
    
    /**
     * 查询对象列表 - 核心查询方法
     * 
     * 执行流程：
     * 1. 获取映射语句配置
     * 2. 执行安全检查（SQL注入防护）
     * 3. 尝试从缓存获取结果
     * 4. 执行数据库查询
     * 5. 将结果存入缓存
     * 
     * 安全特性：
     * - SQL注入检测
     * - 参数验证
     * - 执行权限检查
     * 
     * 性能优化：
     * - 多级缓存策略
     * - 智能缓存键生成
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)  // 只读事务，提高性能
    public <E> List<E> selectList(String statement, Object parameter) {
        // 1. 获取映射语句配置
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        if (mappedStatement == null) {
            throw new SqlExecutionException("找不到语句: " + statement);
        }
        
        String sql = mappedStatement.getSql();
        Class<?> resultType = mappedStatement.getResultType();
        Object[] params = parseParameters(parameter);
        
        // 2. 三重安全检查 - 防止SQL注入攻击
        SqlInjectionGuard.validateSql(sql);           // SQL语句安全性检查
        SqlInjectionGuard.validateParameters(params); // 参数安全性检查
        SqlInjectionGuard.validateExecutionPermission(sql, "SELECT"); // 执行权限检查
        
        // 3. 缓存机制 - 提高查询性能
        String cacheKey = SqlInjectionGuard.generateSafeCacheKey(sql, params);
        
        // 尝试从缓存获取（一级缓存 -> 二级缓存）
        Object cached = cacheManager.get(cacheKey);
        if (cached != null) {
            logger.debug("缓存命中: {}", statement);
            return (List<E>) cached;
        }
        
        logger.info("执行查询: {} 参数: {}", sql, parameter);
        
        try {
            // 4. 执行数据库查询
            List<E> result;
            if (parameter == null) {
                // 无参数查询
                result = (List<E>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(resultType));
            } else {
                // 带参数查询
                result = (List<E>) jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(resultType));
            }
            
            // 5. 将查询结果存入缓存
            cacheManager.put(cacheKey, result);
            
            return result;
        } catch (Exception e) {
            logger.error("查询执行失败: " + sql, e);
            // 抛出自定义异常，包含详细的错误信息
            throw new SqlExecutionException("查询执行失败: " + e.getMessage(), sql, params, e);
        }
    }
    
    /**
     * 插入操作 - 委托给executeUpdate处理
     */
    @Override
    public int insert(String statement, Object parameter) {
        return executeUpdate(statement, parameter);
    }
    
    /**
     * 更新操作 - 委托给executeUpdate处理
     */
    @Override
    public int update(String statement, Object parameter) {
        return executeUpdate(statement, parameter);
    }
    
    /**
     * 删除操作 - 委托给executeUpdate处理
     */
    @Override
    public int delete(String statement, Object parameter) {
        return executeUpdate(statement, parameter);
    }
    
    /**
     * 执行更新操作（INSERT、UPDATE、DELETE）- 核心更新方法
     * 
     * 执行流程：
     * 1. 获取映射语句配置
     * 2. 执行安全检查
     * 3. 执行数据库更新
     * 4. 清理相关缓存（保证数据一致性）
     * 
     * 关键特性：
     * - 事务支持：自动回滚失败操作
     * - 缓存清理：确保数据一致性
     * - 权限验证：根据操作类型检查权限
     */
    @Transactional
    private int executeUpdate(String statement, Object parameter) {
        // 1. 获取映射语句配置
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        if (mappedStatement == null) {
            throw new SqlExecutionException("找不到语句: " + statement);
        }
        
        String sql = mappedStatement.getSql();
        Object[] params = parseParameters(parameter);
        
        // 2. 安全检查
        SqlInjectionGuard.validateSql(sql);
        SqlInjectionGuard.validateParameters(params);
        
        // 根据SQL类型验证执行权限（INSERT/UPDATE/DELETE权限可能不同）
        String operationType = determineOperationType(sql);
        SqlInjectionGuard.validateExecutionPermission(sql, operationType);
        
        logger.info("执行更新: {} 参数: {}", sql, parameter);
        
        try {
            // 3. 执行数据库更新
            int result;
            if (parameter == null) {
                result = jdbcTemplate.update(sql);
            } else {
                result = jdbcTemplate.update(sql, params);
            }
            
            // 4. 更新操作成功后清除相关缓存
            // 这是关键的数据一致性保证：确保缓存不会返回过期数据
            if (result > 0) {
                clearRelatedCache(statement);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("更新执行失败: " + sql, e);
            throw new SqlExecutionException("更新执行失败: " + e.getMessage(), sql, params, e);
        }
    }
    
    /**
     * 确定操作类型 - 用于权限验证
     * 
     * 通过SQL语句前缀判断操作类型，支持细粒度的权限控制
     */
    private String determineOperationType(String sql) {
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.startsWith("INSERT")) {
            return "INSERT";
        } else if (upperSql.startsWith("UPDATE")) {
            return "UPDATE";
        } else if (upperSql.startsWith("DELETE")) {
            return "DELETE";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 清除相关缓存 - 保证数据一致性
     * 
     * 当数据被修改时，必须清除相关缓存，防止返回过期数据
     * 
     * 清理策略：
     * - 简化版本：清除整个一级缓存
     * - 生产环境可优化为精确清理相关表的缓存
     */
    private void clearRelatedCache(String statement) {
        // 简化版本：清除一级缓存
        // TODO: 未来可以优化为根据表名精确清理相关缓存
        cacheManager.clearSessionCache();
        logger.debug("已清除相关缓存: {}", statement);
    }
    
    /**
     * 简单的参数解析
     * 
     * 处理不同类型的参数输入：
     * - null参数：返回空数组
     * - 数组参数：直接使用
     * - 单个参数：包装成数组
     * 
     * 注意：实际MyBatis有更复杂的参数解析逻辑，包括命名参数、对象属性映射等
     */
    private Object[] parseParameters(Object parameter) {
        if (parameter == null) {
            return new Object[0];
        }
        
        // 如果是数组，直接返回
        if (parameter instanceof Object[]) {
            return (Object[]) parameter;
        }
        
        // 如果是单个参数，包装成数组
        return new Object[]{parameter};
    }
    
    /**
     * 获取Mapper接口代理对象
     * 
     * 通过配置对象创建动态代理，实现接口方法到SQL语句的映射
     */
    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }
    
    /**
     * 获取配置对象
     */
    @Override
    public MyBatisConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * 关闭会话
     * 
     * 在实际实现中，这里会：
     * - 关闭数据库连接
     * - 清理缓存
     * - 释放相关资源
     */
    @Override
    public void close() {
        // 在实际实现中，这里会关闭数据库连接
        logger.info("SQL会话已关闭");
    }
}
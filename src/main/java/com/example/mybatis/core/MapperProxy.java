package com.example.mybatis.core;

import com.example.mybatis.annotation.*;
import com.example.mybatis.dynamic.DynamicSqlProcessor;
import com.example.mybatis.exception.MyBatisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mapper代理类
 * 实现动态代理，拦截Mapper接口方法调用并执行相应的SQL操作
 */
public class MapperProxy<T> implements InvocationHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MapperProxy.class);
    
    /**
     * SQL会话
     */
    private final SqlSession sqlSession;
    
    /**
     * Mapper接口类型
     */
    private final Class<T> mapperInterface;
    
    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果调用的是Object类的方法，直接执行
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        
        // 获取方法的全限定名作为语句ID
        String statementId = mapperInterface.getName() + "." + method.getName();
        
        logger.info("执行Mapper方法: {} 参数: {}", statementId, args);
        
        // 检查方法上的SQL注解并执行相应操作
        if (method.isAnnotationPresent(Select.class)) {
            return executeSelect(method, statementId, args);
        } else if (method.isAnnotationPresent(SelectIf.class)) {
            return executeSelectIf(method, statementId, args);
        } else if (method.isAnnotationPresent(Insert.class)) {
            return executeInsert(statementId, args);
        } else if (method.isAnnotationPresent(Update.class)) {
            return executeUpdate(statementId, args);
        } else if (method.isAnnotationPresent(Delete.class)) {
            return executeDelete(statementId, args);
        } else {
            throw new MyBatisException("MISSING_SQL_ANNOTATION", 
                "方法 " + method.getName() + " 没有找到对应的SQL注解");
        }
    }
    
    /**
     * 执行查询操作
     */
    private Object executeSelect(Method method, String statementId, Object[] args) {
        Object parameter = args != null && args.length > 0 ? args[0] : null;
        
        // 判断返回类型是否为List
        if (method.getReturnType().isAssignableFrom(java.util.List.class)) {
            return sqlSession.selectList(statementId, parameter);
        } else {
            return sqlSession.selectOne(statementId, parameter);
        }
    }
    
    /**
     * 执行条件查询操作
     */
    private Object executeSelectIf(Method method, String statementId, Object[] args) {
        SelectIf selectIf = method.getAnnotation(SelectIf.class);
        String sql = selectIf.value();
        String condition = selectIf.condition();
        String elseSql = selectIf.elseSql();
        
        // 处理动态SQL
        try {
            String finalSql = DynamicSqlProcessor.processDynamicSql(sql, condition, elseSql, method, args);
            DynamicSqlProcessor.validateDynamicSql(finalSql, condition);
            
            // 临时创建MappedStatement
            MappedStatement dynamicStatement = createDynamicMappedStatement(statementId, finalSql, method);
            
            // 执行查询
            Object parameter = args != null && args.length > 0 ? args[0] : null;
            
            if (method.getReturnType().isAssignableFrom(java.util.List.class)) {
                return sqlSession.selectList(statementId + "_dynamic", parameter);
            } else {
                return sqlSession.selectOne(statementId + "_dynamic", parameter);
            }
        } catch (Exception e) {
            logger.error("动态SQL执行失败: {}", e.getMessage(), e);
            throw new MyBatisException("DYNAMIC_SQL_ERROR", 
                "动态SQL执行失败: " + e.getMessage(), sql, args, e);
        }
    }
    
    /**
     * 创建动态MappedStatement
     */
    private MappedStatement createDynamicMappedStatement(String statementId, String sql, Method method) {
        // 获取返回类型
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(java.util.List.class)) {
            // 尝试获取泛型类型
            returnType = Object.class; // 简化处理
        }
        
        MappedStatement statement = new MappedStatement(
            statementId + "_dynamic",
            MappedStatement.SqlCommandType.SELECT,
            sql,
            returnType
        );
        
        // 临时注册到配置中
        sqlSession.getConfiguration().addMappedStatement(statementId + "_dynamic", statement);
        
        return statement;
    }
    
    /**
     * 执行插入操作
     */
    private Object executeInsert(String statementId, Object[] args) {
        Object parameter = args != null && args.length > 0 ? args[0] : null;
        return sqlSession.insert(statementId, parameter);
    }
    
    /**
     * 执行更新操作
     */
    private Object executeUpdate(String statementId, Object[] args) {
        Object parameter = args != null && args.length > 0 ? args[0] : null;
        return sqlSession.update(statementId, parameter);
    }
    
    /**
     * 执行删除操作
     */
    private Object executeDelete(String statementId, Object[] args) {
        Object parameter = args != null && args.length > 0 ? args[0] : null;
        return sqlSession.delete(statementId, parameter);
    }
} 
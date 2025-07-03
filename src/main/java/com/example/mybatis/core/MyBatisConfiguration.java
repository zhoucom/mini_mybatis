package com.example.mybatis.core;

import com.example.mybatis.cache.CacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis配置类
 * 负责管理数据源、映射语句等核心配置
 */
public class MyBatisConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(MyBatisConfiguration.class);
    
    /**
     * 数据源
     */
    private DataSource dataSource;
    
    /**
     * 缓存配置
     */
    private CacheConfig cacheConfig;
    
    /**
     * 映射语句缓存
     * key: 方法全限定名 (className.methodName)
     * value: MappedStatement对象
     */
    private final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();
    
    /**
     * Mapper代理工厂缓存
     */
    private final Map<Class<?>, MapperProxyFactory<?>> mapperProxyFactories = new ConcurrentHashMap<>();
    
    public MyBatisConfiguration() {
    }
    
    public MyBatisConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * 添加映射语句
     */
    public void addMappedStatement(String id, MappedStatement mappedStatement) {
        mappedStatements.put(id, mappedStatement);
    }
    
    /**
     * 获取映射语句
     */
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }
    
    /**
     * 添加Mapper
     */
    public <T> void addMapper(Class<T> type) {
        logger.info("注册 Mapper: {}", type.getName());
        mapperProxyFactories.put(type, new MapperProxyFactory<>(type, this));
        logger.info("Mapper 注册完成，当前已注册 {} 个 Mapper", mapperProxyFactories.size());
    }
    
    /**
     * 获取Mapper
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        logger.info("MyBatisConfiguration.getMapper() 被调用，type: {}, sqlSession: {}", type.getName(), sqlSession);
        
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) mapperProxyFactories.get(type);
        if (mapperProxyFactory == null) {
            logger.error("找不到 MapperProxyFactory，type: {}", type.getName());
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
        
        logger.info("找到 MapperProxyFactory: {}", mapperProxyFactory);
        return mapperProxyFactory.newInstance(sqlSession);
    }
    
    /**
     * 检查是否已注册Mapper
     */
    public boolean hasMapper(Class<?> type) {
        return mapperProxyFactories.containsKey(type);
    }
    
    // Getters and Setters
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }
    
    public void setCacheConfig(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }
    
    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }
    
    public Map<Class<?>, MapperProxyFactory<?>> getMapperProxyFactories() {
        return mapperProxyFactories;
    }
} 
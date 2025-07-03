package com.example.mybatis.core;

import com.example.mybatis.cache.CacheConfig;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis配置类
 * 负责管理数据源、映射语句等核心配置
 */
public class MyBatisConfig {
    
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
    
    public MyBatisConfig() {
    }
    
    public MyBatisConfig(DataSource dataSource) {
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
        mapperProxyFactories.put(type, new MapperProxyFactory<>(type));
    }
    
    /**
     * 获取Mapper
     */
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) mapperProxyFactories.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + " is not known to the MapperRegistry.");
        }
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
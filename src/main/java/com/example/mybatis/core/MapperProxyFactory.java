package com.example.mybatis.core;

import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * Mapper代理工厂
 * 负责创建Mapper接口的动态代理对象
 */
public class MapperProxyFactory<T> implements SmartFactoryBean<T> {

    private static final Logger logger = LoggerFactory.getLogger(MapperProxyFactory.class);

    /**
     * Mapper接口类型
     */
    private Class<T> mapperInterface;

    /**
     * MyBatis配置
     */
    private MyBatisConfiguration configuration;

    @Autowired
    private SqlSession sqlSession;

    public MapperProxyFactory() {
        logger.info("MapperProxyFactory 无参构造函数被调用");
    }

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.configuration = null;
        logger.info("MapperProxyFactory 构造函数被调用 (无配置): {}", mapperInterface.getName());
    }

    public MapperProxyFactory(Class<T> mapperInterface, MyBatisConfiguration configuration) {
        this.mapperInterface = mapperInterface;
        this.configuration = configuration;
        logger.info("MapperProxyFactory 构造函数被调用 (有配置): {}, configuration: {}", 
                   mapperInterface.getName(), configuration);
    }

    @Override
    public T getObject() throws Exception {
        logger.info("MapperProxyFactory.getObject() 被调用，mapperInterface: {}, configuration: {}, sqlSession: {}",
                   mapperInterface.getName(), configuration, sqlSession);

        if (configuration != null) {
            logger.info("使用 configuration.getMapper() 创建代理");
            return configuration.getMapper(mapperInterface, sqlSession);
        } else {
            logger.warn("configuration 为 null，使用 newInstance() 创建代理");
            return newInstance(sqlSession);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean isEagerInit() {
        // 允许 Spring 在启动阶段就初始化，以便尽早解析类型
        return false;
    }

    /**
     * 创建代理实例
     */
    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(
                mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                mapperProxy);
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setConfiguration(MyBatisConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        // 支持 Spring 的属性注入，确保 mapperInterface 被正确赋值
        logger.info("setMapperInterface 被调用: {}", mapperInterface.getName());
        this.mapperInterface = mapperInterface;
    }
}
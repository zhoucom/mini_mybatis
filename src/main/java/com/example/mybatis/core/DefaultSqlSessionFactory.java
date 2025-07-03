package com.example.mybatis.core;

/**
 * 默认SQL会话工厂实现
 * 负责创建DefaultSqlSession实例
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    
    /**
     * MyBatis配置对象
     */
    private final MyBatisConfiguration configuration;
    
    public DefaultSqlSessionFactory(MyBatisConfiguration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
    
    @Override
    public MyBatisConfiguration getConfiguration() {
        return configuration;
    }
} 
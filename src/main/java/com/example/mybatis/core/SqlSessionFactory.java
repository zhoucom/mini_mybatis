package com.example.mybatis.core;

/**
 * SQL会话工厂接口
 * 负责创建SqlSession实例
 */
public interface SqlSessionFactory {
    
    /**
     * 创建SQL会话
     */
    SqlSession openSession();
    
    /**
     * 获取配置对象
     */
    MyBatisConfiguration getConfiguration();
} 
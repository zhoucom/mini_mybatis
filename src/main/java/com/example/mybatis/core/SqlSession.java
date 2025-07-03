package com.example.mybatis.core;

import java.util.List;

/**
 * SQL会话接口 - 定义SQL执行的核心方法
 */
public interface SqlSession {
    
    /**
     * 查询单个对象
     */
    <T> T selectOne(String statement, Object parameter);
    
    /**
     * 查询对象列表
     */
    <E> List<E> selectList(String statement, Object parameter);
    
    /**
     * 插入数据
     */
    int insert(String statement, Object parameter);
    
    /**
     * 更新数据
     */
    int update(String statement, Object parameter);
    
    /**
     * 删除数据
     */
    int delete(String statement, Object parameter);
    
    /**
     * 获取Mapper代理对象
     */
    <T> T getMapper(Class<T> type);
    
    /**
     * 获取配置对象
     */
    MyBatisConfiguration getConfiguration();
    
    /**
     * 关闭会话
     */
    void close();
} 
package com.example.mybatis.core;

/**
 * 映射语句 - 封装SQL语句和相关信息
 */
public class MappedStatement {
    
    /**
     * 语句ID（通常是方法全限定名）
     */
    private String id;
    
    /**
     * SQL类型枚举
     */
    public enum SqlCommandType {
        SELECT, INSERT, UPDATE, DELETE
    }
    
    /**
     * SQL命令类型
     */
    private SqlCommandType sqlCommandType;
    
    /**
     * SQL语句
     */
    private String sql;
    
    /**
     * 返回类型
     */
    private Class<?> resultType;
    
    public MappedStatement() {
    }
    
    public MappedStatement(String id, SqlCommandType sqlCommandType, String sql, Class<?> resultType) {
        this.id = id;
        this.sqlCommandType = sqlCommandType;
        this.sql = sql;
        this.resultType = resultType;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }
    
    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }
    
    public String getSql() {
        return sql;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public Class<?> getResultType() {
        return resultType;
    }
    
    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }
    
    @Override
    public String toString() {
        return "MappedStatement{" +
                "id='" + id + '\'' +
                ", sqlCommandType=" + sqlCommandType +
                ", sql='" + sql + '\'' +
                ", resultType=" + resultType +
                '}';
    }
} 
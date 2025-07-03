package com.example.mybatis.exception;

/**
 * SQL执行异常
 */
public class SqlExecutionException extends MyBatisException {
    
    public SqlExecutionException(String message) {
        super("SQL_EXECUTION_ERROR", message);
    }
    
    public SqlExecutionException(String message, Throwable cause) {
        super("SQL_EXECUTION_ERROR", message, cause);
    }
    
    public SqlExecutionException(String message, String sqlStatement, Object[] parameters) {
        super("SQL_EXECUTION_ERROR", message, sqlStatement, parameters);
    }
    
    public SqlExecutionException(String message, String sqlStatement, Object[] parameters, Throwable cause) {
        super("SQL_EXECUTION_ERROR", message, sqlStatement, parameters, cause);
    }
} 
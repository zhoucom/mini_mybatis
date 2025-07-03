package com.example.mybatis.exception;

/**
 * MyBatis自定义异常基类
 */
public class MyBatisException extends RuntimeException {
    
    private String errorCode;
    private String sqlStatement;
    private Object[] parameters;
    
    public MyBatisException(String message) {
        super(message);
    }
    
    public MyBatisException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MyBatisException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public MyBatisException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public MyBatisException(String errorCode, String message, String sqlStatement, Object[] parameters) {
        super(message);
        this.errorCode = errorCode;
        this.sqlStatement = sqlStatement;
        this.parameters = parameters;
    }
    
    public MyBatisException(String errorCode, String message, String sqlStatement, Object[] parameters, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.sqlStatement = sqlStatement;
        this.parameters = parameters;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getSqlStatement() {
        return sqlStatement;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }
        
        sb.append(super.getMessage());
        
        if (sqlStatement != null) {
            sb.append("\nSQL: ").append(sqlStatement);
        }
        
        if (parameters != null && parameters.length > 0) {
            sb.append("\nParameters: ");
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameters[i]);
            }
        }
        
        return sb.toString();
    }
} 
package com.example.mybatis.security;

import com.example.mybatis.exception.MyBatisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL注入防护器
 * 提供SQL注入检测和防护功能
 */
public class SqlInjectionGuard {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlInjectionGuard.class);
    
    /**
     * 危险的SQL关键词
     */
    private static final Set<String> DANGEROUS_KEYWORDS = new HashSet<>(Arrays.asList(
        "DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE", "EXEC", "EXECUTE",
        "UNION", "SCRIPT", "JAVASCRIPT", "VBSCRIPT", "ONLOAD", "ONERROR",
        "EVAL", "EXPRESSION", "ALERT", "CONFIRM", "PROMPT"
    ));
    
    /**
     * SQL注入攻击模式
     */
    private static final Pattern[] INJECTION_PATTERNS = {
        Pattern.compile("('.+--)|(--.+)", Pattern.CASE_INSENSITIVE),  // SQL注释
        Pattern.compile("('.+(;|;.+))", Pattern.CASE_INSENSITIVE),    // 分号结束语句
        Pattern.compile("('.+\\|\\|.+)", Pattern.CASE_INSENSITIVE),   // 字符串拼接
        Pattern.compile("('.+union.+select.+)", Pattern.CASE_INSENSITIVE), // UNION注入
        Pattern.compile("('.+or.+1.+=.+1.+)", Pattern.CASE_INSENSITIVE),   // OR永真条件
        Pattern.compile("('.+and.+1.+=.+1.+)", Pattern.CASE_INSENSITIVE),  // AND永真条件
        Pattern.compile("('.+exec.+)", Pattern.CASE_INSENSITIVE),      // 执行命令
        Pattern.compile("('.+script.+)", Pattern.CASE_INSENSITIVE)     // 脚本注入
    };
    
    /**
     * 验证SQL语句安全性
     */
    public static void validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new MyBatisException("SQL_VALIDATION_ERROR", "SQL语句不能为空");
        }
        
        String upperSql = sql.toUpperCase().trim();
        
        // 检查是否包含危险关键词
        for (String keyword : DANGEROUS_KEYWORDS) {
            // 使用\b保证关键词为独立单词，避免"CREATED_TIME"等误判
            String pattern = "\\b" + keyword + "\\b"; // 大写SQL已处理
            if (Pattern.compile(pattern).matcher(upperSql).find()) {
                if (!isParameterizedSql(sql)) {
                    logger.warn("检测到潜在的SQL注入风险: {}", keyword);
                    throw new MyBatisException("SQL_INJECTION_DETECTED", 
                        "检测到危险的SQL关键词: " + keyword, sql, null);
                }
            }
        }
        
        // 检查SQL注入攻击模式
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(sql).find()) {
                logger.warn("检测到SQL注入攻击模式: {}", sql);
                throw new MyBatisException("SQL_INJECTION_DETECTED", 
                    "检测到SQL注入攻击模式", sql, null);
            }
        }
    }
    
    /**
     * 验证参数安全性
     */
    public static void validateParameters(Object[] parameters) {
        if (parameters == null) {
            return;
        }
        
        for (Object param : parameters) {
            if (param instanceof String) {
                String strParam = (String) param;
                validateStringParameter(strParam);
            }
        }
    }
    
    /**
     * 验证字符串参数
     */
    private static void validateStringParameter(String param) {
        if (param == null) {
            return;
        }
        
        String upperParam = param.toUpperCase();
        
        // 检查参数中是否包含危险关键词
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperParam.contains(keyword)) {
                logger.warn("参数中检测到危险关键词: {} in {}", keyword, param);
                // 对于参数，我们记录警告但不阻止执行，因为可能是合法的数据
            }
        }
        
        // 检查明显的注入尝试
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(param).find()) {
                logger.error("参数中检测到SQL注入尝试: {}", param);
                throw new MyBatisException("PARAMETER_INJECTION_DETECTED", 
                    "参数中检测到SQL注入尝试: " + param);
            }
        }
    }
    
    /**
     * 检查是否为参数化SQL
     */
    private static boolean isParameterizedSql(String sql) {
        return sql.contains("?") || sql.contains("#{") || sql.contains("${");
    }
    
    /**
     * 清理和转义危险字符
     */
    public static String escapeSql(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("'", "''")           // 转义单引号
            .replace("\"", "\\\"")        // 转义双引号
            .replace("\\", "\\\\")        // 转义反斜杠
            .replace(";", "\\;")          // 转义分号
            .replace("--", "\\-\\-")      // 转义注释
            .replace("/*", "\\/*")        // 转义多行注释开始
            .replace("*/", "\\*/");       // 转义多行注释结束
    }
    
    /**
     * 验证SQL执行权限
     */
    public static void validateExecutionPermission(String sql, String operation) {
        String upperSql = sql.toUpperCase().trim();
        
        // 根据操作类型验证权限
        switch (operation.toUpperCase()) {
            case "SELECT":
                if (!upperSql.startsWith("SELECT")) {
                    throw new MyBatisException("PERMISSION_DENIED", 
                        "SELECT操作只能执行SELECT语句");
                }
                break;
            case "INSERT":
                if (!upperSql.startsWith("INSERT")) {
                    throw new MyBatisException("PERMISSION_DENIED", 
                        "INSERT操作只能执行INSERT语句");
                }
                break;
            case "UPDATE":
                if (!upperSql.startsWith("UPDATE")) {
                    throw new MyBatisException("PERMISSION_DENIED", 
                        "UPDATE操作只能执行UPDATE语句");
                }
                break;
            case "DELETE":
                if (!upperSql.startsWith("DELETE")) {
                    throw new MyBatisException("PERMISSION_DENIED", 
                        "DELETE操作只能执行DELETE语句");
                }
                break;
            default:
                logger.warn("未知的操作类型: {}", operation);
        }
    }
    
    /**
     * 生成安全的缓存key
     */
    public static String generateSafeCacheKey(String sql, Object[] parameters) {
        StringBuilder key = new StringBuilder();
        key.append(sql.hashCode());
        
        if (parameters != null) {
            for (Object param : parameters) {
                key.append("_");
                if (param != null) {
                    key.append(param.toString().hashCode());
                } else {
                    key.append("null");
                }
            }
        }
        
        return key.toString();
    }
} 
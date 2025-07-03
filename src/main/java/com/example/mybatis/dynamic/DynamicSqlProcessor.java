package com.example.mybatis.dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态SQL处理器
 * 支持简单的条件判断和参数替换
 */
public class DynamicSqlProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicSqlProcessor.class);
    
    /**
     * 条件表达式模式
     */
    private static final Pattern CONDITION_PATTERN = Pattern.compile(
        "(\\w+)\\s*(==|!=|>|<|>=|<=|is\\s+null|is\\s+not\\s+null)\\s*(\\w+|null|'[^']*'|\"[^\"]*\"|\\d+)"
    );
    
    /**
     * 处理动态SQL
     */
    public static String processDynamicSql(String sql, String condition, String elseSql, 
                                         Method method, Object[] args) {
        
        if (condition == null || "true".equals(condition)) {
            return sql;
        }
        
        // 构建参数上下文
        Map<String, Object> context = buildParameterContext(method, args);
        
        // 评估条件
        boolean conditionResult = evaluateCondition(condition, context);
        
        logger.debug("动态SQL条件评估: {} = {}", condition, conditionResult);
        
        if (conditionResult) {
            return sql;
        } else if (elseSql != null && !elseSql.isEmpty()) {
            return elseSql;
        } else {
            // 如果条件不满足且没有else语句，返回空查询
            return "SELECT 1 WHERE 1=0"; // 返回空结果集
        }
    }
    
    /**
     * 构建参数上下文
     */
    private static Map<String, Object> buildParameterContext(Method method, Object[] args) {
        Map<String, Object> context = new HashMap<>();
        
        if (args == null || args.length == 0) {
            return context;
        }
        
        // 简单的参数名映射 (实际MyBatis会解析@Param注解)
        String[] paramNames = getParameterNames(method, args.length);
        for (int i = 0; i < args.length && i < paramNames.length; i++) {
            context.put(paramNames[i], args[i]);
        }
        
        return context;
    }
    
    /**
     * 获取参数名称
     */
    private static String[] getParameterNames(Method method, int paramCount) {
        // 简化版本：使用通用参数名
        String[] names = new String[paramCount];
        for (int i = 0; i < paramCount; i++) {
            names[i] = "param" + (i + 1);
        }
        return names;
    }
    
    /**
     * 评估条件表达式
     */
    private static boolean evaluateCondition(String condition, Map<String, Object> context) {
        if (condition == null || condition.trim().isEmpty()) {
            return true;
        }
        
        condition = condition.trim().toLowerCase();
        
        // 处理简单的条件表达式
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        if (matcher.find()) {
            String paramName = matcher.group(1);
            String operator = matcher.group(2);
            String value = matcher.group(3);
            
            Object paramValue = context.get(paramName);
            
            return evaluateComparison(paramValue, operator, value);
        }
        
        // 处理特殊条件
        if (condition.contains("!=") && condition.contains("null")) {
            String paramName = extractParameterName(condition);
            Object paramValue = context.get(paramName);
            return paramValue != null;
        }
        
        if (condition.contains("==") && condition.contains("null")) {
            String paramName = extractParameterName(condition);
            Object paramValue = context.get(paramName);
            return paramValue == null;
        }
        
        // 其他复杂条件处理
        return evaluateComplexCondition(condition, context);
    }
    
    /**
     * 评估比较操作
     */
    private static boolean evaluateComparison(Object paramValue, String operator, String value) {
        switch (operator.toLowerCase()) {
            case "==":
                return isEqual(paramValue, value);
            case "!=":
                return !isEqual(paramValue, value);
            case ">":
                return compareNumeric(paramValue, value) > 0;
            case "<":
                return compareNumeric(paramValue, value) < 0;
            case ">=":
                return compareNumeric(paramValue, value) >= 0;
            case "<=":
                return compareNumeric(paramValue, value) <= 0;
            case "is null":
                return paramValue == null;
            case "is not null":
                return paramValue != null;
            default:
                logger.warn("未支持的操作符: {}", operator);
                return false;
        }
    }
    
    /**
     * 判断相等
     */
    private static boolean isEqual(Object paramValue, String value) {
        if (paramValue == null) {
            return "null".equals(value);
        }
        
        // 移除引号
        if (value.startsWith("'") && value.endsWith("'")) {
            value = value.substring(1, value.length() - 1);
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        
        return paramValue.toString().equals(value);
    }
    
    /**
     * 数值比较
     */
    private static int compareNumeric(Object paramValue, String value) {
        if (paramValue == null) {
            return -1;
        }
        
        try {
            double param = Double.parseDouble(paramValue.toString());
            double val = Double.parseDouble(value);
            return Double.compare(param, val);
        } catch (NumberFormatException e) {
            logger.warn("数值比较失败: {} vs {}", paramValue, value);
            return 0;
        }
    }
    
    /**
     * 提取参数名
     */
    private static String extractParameterName(String condition) {
        String[] parts = condition.split("\\s+");
        if (parts.length > 0) {
            return parts[0];
        }
        return "";
    }
    
    /**
     * 评估复杂条件
     */
    private static boolean evaluateComplexCondition(String condition, Map<String, Object> context) {
        // 处理 AND 条件
        if (condition.contains(" and ")) {
            String[] parts = condition.split(" and ");
            for (String part : parts) {
                if (!evaluateCondition(part.trim(), context)) {
                    return false;
                }
            }
            return true;
        }
        
        // 处理 OR 条件
        if (condition.contains(" or ")) {
            String[] parts = condition.split(" or ");
            for (String part : parts) {
                if (evaluateCondition(part.trim(), context)) {
                    return true;
                }
            }
            return false;
        }
        
        // 简单的参数存在性检查
        if (context.containsKey(condition)) {
            Object value = context.get(condition);
            return value != null && !"".equals(value.toString());
        }
        
        logger.warn("无法评估条件: {}", condition);
        return false;
    }
    
    /**
     * 处理SQL中的参数占位符
     */
    public static String processParameterPlaceholders(String sql, Object[] parameters) {
        if (sql == null || parameters == null) {
            return sql;
        }
        
        // 简单的参数替换（实际MyBatis有更复杂的处理）
        String result = sql;
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            String placeholder = "#{param" + (i + 1) + "}";
            
            if (result.contains(placeholder)) {
                String replacement = param != null ? param.toString() : "null";
                result = result.replace(placeholder, "?");
            }
        }
        
        return result;
    }
    
    /**
     * 验证动态SQL的安全性
     */
    public static void validateDynamicSql(String sql, String condition) {
        if (sql == null) {
            throw new IllegalArgumentException("动态SQL不能为空");
        }
        
        if (condition != null && condition.contains("${")) {
            logger.warn("动态条件中包含不安全的参数替换: {}", condition);
        }
        
        // 检查SQL注入风险
        if (sql.contains("${") && !sql.contains("#{")) {
            logger.warn("SQL中包含不安全的参数替换: {}", sql);
        }
    }
} 
package com.example.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 条件查询注解 - 支持动态SQL
 * 当指定条件为true时执行SQL
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelectIf {
    /**
     * SQL查询语句
     */
    String value();
    
    /**
     * 条件表达式
     * 支持参数名引用，如：name != null, age > 0
     */
    String condition() default "true";
    
    /**
     * 当条件不满足时的默认SQL
     */
    String elseSql() default "";
} 
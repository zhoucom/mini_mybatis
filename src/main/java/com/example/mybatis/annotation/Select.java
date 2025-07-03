package com.example.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 查询注解 - 用于标记查询SQL方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    /**
     * SQL查询语句
     */
    String value();
} 
package com.example.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 插入注解 - 用于标记插入SQL方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
    /**
     * SQL插入语句
     */
    String value();
} 
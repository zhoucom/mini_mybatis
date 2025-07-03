package com.example.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 删除注解 - 用于标记删除SQL方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
    /**
     * SQL删除语句
     */
    String value();
} 
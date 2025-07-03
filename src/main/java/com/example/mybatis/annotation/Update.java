package com.example.mybatis.annotation;

import java.lang.annotation.*;

/**
 * 更新注解 - 用于标记更新SQL方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
    /**
     * SQL更新语句
     */
    String value();
} 
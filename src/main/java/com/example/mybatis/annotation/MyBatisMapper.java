package com.example.mybatis.annotation;

import org.springframework.stereotype.Component;
import java.lang.annotation.*;

/**
 * MyBatis Mapper标记注解
 * 用于标识需要被动态代理的Mapper接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface MyBatisMapper {
} 
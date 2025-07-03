package com.yourcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Mini MyBatis Gradle示例应用
 * 
 * 这个示例展示了如何在Gradle项目中集成Mini MyBatis Starter
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.yourcompany", "com.example.mybatis"})
public class GradleExampleApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GradleExampleApplication.class, args);
        
        System.out.println("==========================================");
        System.out.println("正在检查 Spring 容器中的 Bean...");
        System.out.println("------------------------------------------");
        
        // 打印所有 Bean 名称
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("mapper")) {
                System.out.println("找到 Mapper Bean: " + beanName);
            }
        }
        
        // 特别检查 AdvancedProductMapper
        try {
            Object mapper = context.getBean("advancedProductMapper");
            System.out.println("✅ AdvancedProductMapper 已成功注入: " + mapper);
        } catch (Exception e) {
            System.out.println("❌ AdvancedProductMapper 注入失败: " + e.getMessage());
        }
        
        System.out.println("------------------------------------------");
        System.out.println("🚀 Mini MyBatis Gradle示例启动成功!");
        System.out.println("📱 API文档: http://localhost:8080/api/products");
        System.out.println("🗄️ H2控制台: http://localhost:8080/h2-console");
        System.out.println("==========================================");
    }
} 
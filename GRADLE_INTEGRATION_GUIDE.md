# Mini MyBatis Starter - Gradle项目接入指南

## 🎯 概述

本指南详细说明如何在Gradle项目中集成Mini MyBatis Spring Boot Starter，实现快速的数据库操作功能。

## 📋 前置条件

- JDK 8+
- Gradle 6.0+
- Spring Boot 2.x

## 🚀 快速接入

### 1. 构建并发布Starter

首先需要将Mini MyBatis Starter安装到本地Maven仓库：

```bash
# 进入starter项目根目录
cd mybatis

# 构建并安装到本地Maven仓库
mvn clean install
```

### 2. Gradle项目配置

#### 2.1 在`build.gradle`中添加依赖

```gradle
plugins {
    id 'org.springframework.boot' version '2.7.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.yourcompany'
version = '1.0.0'
sourceCompatibility = '8'

repositories {
    mavenLocal()  // 重要：添加本地Maven仓库
    mavenCentral()
}

dependencies {
    // Spring Boot基础依赖
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    
    // Mini MyBatis Starter
    implementation 'com.example:mini-mybatis-spring-boot-starter:1.0.0'
    
    // 数据库驱动 (根据实际数据库选择)
    runtimeOnly 'com.h2database:h2:2.1.210'              // H2数据库
    // runtimeOnly 'mysql:mysql-connector-java:8.0.33'   // MySQL
    // runtimeOnly 'org.postgresql:postgresql:42.5.4'    // PostgreSQL
    
    // 测试依赖
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```

#### 2.2 Kotlin DSL版本 (`build.gradle.kts`)

```kotlin
plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.yourcompany"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()  // 重要：添加本地Maven仓库
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    
    // Mini MyBatis Starter
    implementation("com.example:mini-mybatis-spring-boot-starter:1.0.0")
    
    // 数据库驱动
    runtimeOnly("com.h2database:h2:2.1.210")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### 3. 应用配置

#### 3.1 `application.yml`配置

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  
  # 数据库初始化
  sql:
    init:
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
      mode: always

# Mini MyBatis配置
mini:
  mybatis:
    mapper-locations: com.yourcompany.mapper
    show-sql: true
    query-timeout: 30

# 日志配置
logging:
  level:
    com.example.mybatis: DEBUG
    com.yourcompany: DEBUG
```

#### 3.2 `application.properties`配置（可选）

```properties
# 数据源配置
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Mini MyBatis配置
mini.mybatis.mapper-locations=com.yourcompany.mapper
mini.mybatis.show-sql=true
mini.mybatis.query-timeout=30

# 日志配置
logging.level.com.example.mybatis=DEBUG
logging.level.com.yourcompany=DEBUG
```

## 📝 代码示例

### 1. 创建实体类

```java
// src/main/java/com/yourcompany/entity/Product.java
package com.yourcompany.entity;

public class Product {
    private Long id;
    private String name;
    private Double price;
    private String category;
    
    // 构造函数
    public Product() {}
    
    public Product(String name, Double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    // Getter和Setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + 
               ", category='" + category + "'}";
    }
}
```

### 2. 创建Mapper接口

```java
// src/main/java/com/yourcompany/mapper/ProductMapper.java
package com.yourcompany.mapper;

import com.yourcompany.entity.Product;
import com.example.mybatis.annotation.*;
import java.util.List;

@MyBatisMapper
public interface ProductMapper {
    
    @Select("SELECT id, name, price, category FROM products WHERE id = ?")
    Product findById(Long id);
    
    @Select("SELECT id, name, price, category FROM products ORDER BY id")
    List<Product> findAll();
    
    @Select("SELECT id, name, price, category FROM products WHERE category = ?")
    List<Product> findByCategory(String category);
    
    @Select("SELECT id, name, price, category FROM products WHERE price BETWEEN ? AND ?")
    List<Product> findByPriceRange(Double minPrice, Double maxPrice);
    
    @Insert("INSERT INTO products(name, price, category) VALUES(?, ?, ?)")
    int insert(String name, Double price, String category);
    
    @Update("UPDATE products SET name = ?, price = ?, category = ? WHERE id = ?")
    int update(String name, Double price, String category, Long id);
    
    @Delete("DELETE FROM products WHERE id = ?")
    int deleteById(Long id);
    
    @Select("SELECT COUNT(*) FROM products")
    Long count();
}
```

### 3. 创建Service层

```java
// src/main/java/com/yourcompany/service/ProductService.java
package com.yourcompany.service;

import com.yourcompany.entity.Product;
import com.yourcompany.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productMapper.findByCategory(category);
    }
    
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productMapper.findByPriceRange(minPrice, maxPrice);
    }
    
    public boolean createProduct(String name, Double price, String category) {
        return productMapper.insert(name, price, category) > 0;
    }
    
    public boolean updateProduct(Long id, String name, Double price, String category) {
        return productMapper.update(name, price, category, id) > 0;
    }
    
    public boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) > 0;
    }
    
    public Long getProductCount() {
        return productMapper.count();
    }
}
```

### 4. 创建Controller层

```java
// src/main/java/com/yourcompany/controller/ProductController.java
package com.yourcompany.controller;

import com.yourcompany.entity.Product;
import com.yourcompany.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public Map<String, Object> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", products);
        result.put("total", products.size());
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        Map<String, Object> result = new HashMap<>();
        if (product != null) {
            result.put("success", true);
            result.put("data", product);
        } else {
            result.put("success", false);
            result.put("message", "商品不存在");
        }
        return result;
    }
    
    @GetMapping("/category/{category}")
    public Map<String, Object> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", products);
        result.put("total", products.size());
        return result;
    }
    
    @PostMapping
    public Map<String, Object> createProduct(@RequestBody Product product) {
        boolean success = productService.createProduct(
            product.getName(), product.getPrice(), product.getCategory());
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "商品创建成功" : "商品创建失败");
        return result;
    }
}
```

### 5. 数据库初始化脚本

```sql
-- src/main/resources/schema.sql
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

```sql
-- src/main/resources/data.sql
INSERT INTO products (name, price, category) VALUES 
('笔记本电脑', 5999.00, '电子产品'),
('无线鼠标', 89.00, '电子产品'),
('机械键盘', 299.00, '电子产品'),
('办公椅', 899.00, '家具'),
('台灯', 199.00, '家具');
```

### 6. 主应用类

```java
// src/main/java/com/yourcompany/Application.java
package com.yourcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 🧪 测试示例

### 1. 单元测试

```java
// src/test/java/com/yourcompany/ProductMapperTest.java
package com.yourcompany;

import com.yourcompany.entity.Product;
import com.yourcompany.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductMapperTest {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Test
    public void testFindById() {
        Product product = productMapper.findById(1L);
        assertNotNull(product);
        assertEquals("笔记本电脑", product.getName());
    }
    
    @Test
    public void testFindAll() {
        List<Product> products = productMapper.findAll();
        assertNotNull(products);
        assertTrue(products.size() > 0);
    }
    
    @Test
    public void testInsert() {
        int result = productMapper.insert("测试商品", 99.99, "测试分类");
        assertEquals(1, result);
    }
}
```

### 2. 集成测试

```java
// src/test/java/com/yourcompany/ProductServiceTest.java
package com.yourcompany;

import com.yourcompany.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {
    
    @Autowired
    private ProductService productService;
    
    @Test
    public void testCreateAndRetrieveProduct() {
        // 创建商品
        boolean created = productService.createProduct("集成测试商品", 199.99, "测试");
        assertTrue(created);
        
        // 验证商品数量增加
        Long count = productService.getProductCount();
        assertTrue(count > 0);
    }
}
```

## 🏃‍♂️ 运行应用

### 1. 使用Gradle运行

```bash
# 构建项目
./gradlew build

# 运行应用
./gradlew bootRun
```

### 2. 测试API

```bash
# 获取所有商品
curl http://localhost:8080/api/products

# 获取指定商品
curl http://localhost:8080/api/products/1

# 按分类查询
curl http://localhost:8080/api/products/category/电子产品

# 创建商品
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"新商品","price":299.99,"category":"新分类"}'
```

## 🔧 高级配置

### 1. 多数据源配置

```yaml
spring:
  datasource:
    primary:
      url: jdbc:mysql://localhost:3306/db1
      username: user1
      password: pass1
    secondary:
      url: jdbc:mysql://localhost:3306/db2
      username: user2
      password: pass2

mini:
  mybatis:
    mapper-locations: com.yourcompany.mapper
    multiple-datasource: true
```

### 2. 生产环境配置

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/production
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

mini:
  mybatis:
    show-sql: false
    query-timeout: 60

logging:
  level:
    com.example.mybatis: INFO
```

## ⚠️ 注意事项

### 1. 版本兼容性
- 确保Spring Boot版本为2.x
- JDK版本需要8+
- Gradle版本建议6.0+

### 2. 包扫描配置
- 确保Mapper接口在配置的扫描路径下
- 建议使用统一的包结构

### 3. 数据库连接
- 生产环境务必配置连接池参数
- 注意数据库驱动的版本兼容性

## 🛠 故障排除

### 1. 常见问题

#### Q: 找不到Mapper Bean
```
NoSuchBeanDefinitionException: No qualifying bean of type 'com.yourcompany.mapper.ProductMapper'
```

**解决方案：**
- 检查`mini.mybatis.mapper-locations`配置
- 确保Mapper接口有`@MyBatisMapper`注解
- 验证包路径是否正确

#### Q: SQL执行异常
```
RuntimeException: 找不到语句: com.yourcompany.mapper.ProductMapper.findById
```

**解决方案：**
- 检查方法是否有SQL注解（@Select、@Insert等）
- 验证SQL语句语法是否正确
- 确保数据库表结构匹配

#### Q: 数据源配置错误
```
Failed to configure a DataSource
```

**解决方案：**
- 检查数据源配置是否完整
- 确保数据库驱动依赖已添加
- 验证数据库连接信息

### 2. 调试技巧

#### 启用详细日志
```yaml
logging:
  level:
    com.example.mybatis: DEBUG
    org.springframework.jdbc: DEBUG
    org.springframework.boot.autoconfigure: DEBUG
```

#### 验证Bean注册
```java
@Component
public class StartupValidator {
    @Autowired
    private ApplicationContext context;
    
    @PostConstruct
    public void validateBeans() {
        System.out.println("已注册的Mapper Beans:");
        String[] mapperBeans = context.getBeanNamesForAnnotation(MyBatisMapper.class);
        for (String bean : mapperBeans) {
            System.out.println("- " + bean);
        }
    }
}
```

## 📚 最佳实践

### 1. 项目结构建议
```
src/main/java/
├── com/yourcompany/
│   ├── Application.java
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── service/         # 业务逻辑
│   ├── mapper/          # Mapper接口
│   └── entity/          # 实体类
```

### 2. 命名规范
- Mapper接口：`XxxMapper`
- 实体类：与数据库表对应
- Service类：`XxxService`
- Controller类：`XxxController`

### 3. SQL编写建议
- 使用参数化查询防止SQL注入
- 复杂查询考虑性能优化
- 添加适当的索引支持

这个详细的接入指南涵盖了从基础配置到高级使用的所有方面，让你可以轻松在任何Gradle项目中集成Mini MyBatis Starter！ 
# Mini MyBatis Gradle 示例项目

这是一个完整的Gradle项目示例，展示了如何集成和使用Mini MyBatis Spring Boot Starter。

## 🎯 项目概述

本项目是一个商品管理系统，通过REST API提供完整的CRUD操作，完美演示了Mini MyBatis的各种功能特性。

## 📁 项目结构

```
gradle-example/
├── build.gradle                           # Gradle构建脚本
├── src/main/java/com/yourcompany/
│   ├── GradleExampleApplication.java      # 主启动类
│   ├── entity/
│   │   └── Product.java                   # 商品实体类
│   ├── mapper/
│   │   └── ProductMapper.java             # 商品Mapper接口
│   ├── service/
│   │   └── ProductService.java            # 商品服务类
│   └── controller/
│       └── ProductController.java         # REST API控制器
├── src/main/resources/
│   ├── application.yml                    # 应用配置
│   ├── schema.sql                         # 数据库表结构
│   └── data.sql                          # 初始化数据
└── README.md                             # 项目说明
```

## 🚀 快速开始

### 1. 前置条件

确保已安装：
- JDK 8+
- Gradle 6.0+
- 已构建Mini MyBatis Starter到本地Maven仓库

### 2. 构建Mini MyBatis Starter

```bash
# 返回到主项目目录
cd ../mybatis

# 构建并安装Starter到本地仓库
mvn clean install
```

### 3. 运行示例项目

```bash
# 回到示例项目目录
cd ../gradle-example

# 构建项目
./gradlew build

# 运行应用
./gradlew bootRun
```

### 4. 验证运行

访问以下URL确认应用正常运行：

- **健康检查**: http://localhost:8080/api/products/health
- **所有商品**: http://localhost:8080/api/products
- **H2控制台**: http://localhost:8080/h2-console

## 📚 功能特性展示

本项目完整展示了Mini MyBatis的核心功能：

- ✅ 动态代理Mapper接口
- ✅ 注解式SQL映射
- ✅ 多种SQL操作（CRUD）
- ✅ 参数自动映射
- ✅ 结果对象映射
- ✅ Spring Boot自动配置
- ✅ REST API集成

## 🧪 API 测试示例

### 查询操作
```bash
# 获取所有商品
curl http://localhost:8080/api/products

# 获取指定商品
curl http://localhost:8080/api/products/1

# 搜索商品
curl "http://localhost:8080/api/products/search?keyword=MacBook"
```

### 管理操作
```bash
# 创建商品
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"新商品","price":999.99,"category":"测试"}'

# 更新商品
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"更新商品","price":1299.99,"category":"更新"}'
```

这个完整的Gradle示例项目是学习Mini MyBatis原理的最佳实践！ 
# Gradle项目快速接入Mini MyBatis Starter

## ✅ 可行性确认

**是的！** 其他Spring Boot项目完全可以通过Gradle直接引入这个Mini MyBatis Starter。

## 🚀 三步快速接入

### 1️⃣ 构建Starter
```bash
cd mybatis
mvn clean install
```

### 2️⃣ 配置依赖 (build.gradle)
```gradle
repositories {
    mavenLocal()  // 重要：添加本地仓库
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    
    // Mini MyBatis Starter
    implementation 'com.example:mini-mybatis-spring-boot-starter:1.0.0'
    
    // 数据库驱动
    runtimeOnly 'com.h2database:h2:2.1.210'
}
```

### 3️⃣ 配置应用 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""

mini:
  mybatis:
    mapper-locations: com.yourcompany.mapper
    show-sql: true
```

## 📝 编写代码

### 实体类
```java
public class User {
    private Long id;
    private String name;
    private String email;
    // getters, setters...
}
```

### Mapper接口
```java
@MyBatisMapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = ?")
    User findById(Long id);
    
    @Insert("INSERT INTO users(name, email) VALUES(?, ?)")
    int insert(String name, String email);
}
```

### Service层
```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
```

## 🧪 完整示例项目

我已创建了完整的Gradle示例项目：

```bash
# 进入示例目录
cd gradle-example

# 一键运行
./run.sh      # Linux/Mac
run.bat       # Windows

# API测试
./test-api.sh
```

## 🎯 核心特性

- ✅ 动态代理Mapper接口
- ✅ 注解式SQL映射
- ✅ 自动参数和结果映射
- ✅ Spring Boot自动配置
- ✅ 完整CRUD操作支持

详细文档请查看：`GRADLE_INTEGRATION_GUIDE.md` 
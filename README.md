# Mini MyBatis Spring Boot Starter

一个简化版的MyBatis实现，帮助理解MyBatis的核心原理。

## 🎯 最新功能 (NEW!)

### 🆕 企业级功能扩展
Mini MyBatis现已支持企业级的高级功能：

- **🔄 动态SQL**: 支持@SelectIf注解，根据条件动态执行SQL
- **💾 多级缓存**: 一级缓存（SqlSession级别）+ 二级缓存（全局级别）
- **🔒 事务管理**: 集成Spring事务，支持声明式事务和回滚
- **🛡️ SQL注入防护**: 多层安全检查，自动阻止SQL注入攻击
- **⚠️ 异常处理**: 友好的错误信息和完整的异常链追踪
- **🔐 参数安全**: 参数化查询，防止代码注入

### 📖 详细文档
- **[高级功能指南](./ADVANCED_FEATURES_GUIDE.md)** - 完整的功能介绍和使用示例
- **[Gradle接入指南](./GRADLE_INTEGRATION_GUIDE.md)** - Gradle项目集成文档
- **[快速开始指南](./GRADLE_QUICK_START.md)** - 三步快速上手

### 🧪 立即体验
```bash
# 1. 启动Gradle示例项目
cd gradle-example
./run.sh

# 2. 运行高级功能测试
./test-advanced-features.sh

# 3. 访问功能演示
curl http://localhost:8080/api/advanced/features
```

## 📚 核心原理解析

### 1. 动态代理机制
```java
// MyBatis的核心是通过动态代理为Mapper接口创建实现类
public class MapperProxy<T> implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 拦截方法调用，解析SQL注解，执行数据库操作
        if (method.isAnnotationPresent(Select.class)) {
            return executeSelect(method, statementId, args);
        }
        // ...其他SQL操作
    }
}
```

### 2. SQL映射与解析
```java
// 通过注解定义SQL映射
public @interface Select {
    String value(); // SQL语句
}

// 封装SQL语句信息
public class MappedStatement {
    private String sql;
    private SqlCommandType sqlCommandType;
    private Class<?> resultType;
}
```

### 3. 会话管理
```java
// SqlSession管理数据库连接和SQL执行
public interface SqlSession {
    <T> T selectOne(String statement, Object parameter);
    <E> List<E> selectList(String statement, Object parameter);
    int insert(String statement, Object parameter);
}
```

### 4. 配置管理
```java
// Configuration管理所有配置信息
public class Configuration {
    private DataSource dataSource;
    private Map<String, MappedStatement> mappedStatements;
    private Map<Class<?>, MapperProxyFactory<?>> mapperProxyFactories;
}
```

## 🚀 快速开始

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>mini-mybatis-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据源
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 

# Mini MyBatis配置
mini:
  mybatis:
    mapper-locations: com.example.mapper
    show-sql: true
    query-timeout: 30
```

### 3. 创建实体类
```java
public class User {
    private Long id;
    private String name;
    private String email;
    
    // getters and setters...
}
```

### 4. 创建Mapper接口
```java
@MyBatisMapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE id = ?")
    User findById(Long id);
    
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    @Insert("INSERT INTO users(name, email) VALUES(?, ?)")
    int insert(String name, String email);
    
    @Update("UPDATE users SET name = ?, email = ? WHERE id = ?")
    int update(String name, String email, Long id);
    
    @Delete("DELETE FROM users WHERE id = ?")
    int deleteById(Long id);
}
```

### 5. 使用Mapper
```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    public int createUser(String name, String email) {
        return userMapper.insert(name, email);
    }
}
```

## 🔧 核心组件说明

### 注解系统
- `@MyBatisMapper`: 标记Mapper接口
- `@Select`: 查询操作注解
- `@Insert`: 插入操作注解  
- `@Update`: 更新操作注解
- `@Delete`: 删除操作注解

### 核心类
- `Configuration`: 配置管理中心
- `SqlSessionFactory`: 会话工厂
- `SqlSession`: SQL执行会话
- `MapperProxy`: Mapper动态代理
- `MappedStatement`: SQL语句封装
- `MapperScanner`: Mapper扫描器

### 自动配置
- `MyBatisAutoConfiguration`: 自动配置类
- `MyBatisProperties`: 配置属性
- `MapperScannerRegistrar`: Mapper注册器

## 🎯 架构流程图

```
用户调用Mapper方法
        ↓
    动态代理拦截
        ↓
   解析SQL注解
        ↓
   查找MappedStatement
        ↓
   SqlSession执行SQL
        ↓
   JdbcTemplate操作数据库
        ↓
   结果映射返回
```

## 🔍 与真实MyBatis的差异

### 简化部分
1. **参数处理**: 简化了复杂的参数解析逻辑
2. **结果映射**: 使用Spring的BeanPropertyRowMapper
3. **缓存机制**: 未实现一级/二级缓存
4. **动态SQL**: 不支持if/foreach等动态标签
5. **插件体系**: 未实现拦截器机制

### 保留核心
1. **动态代理**: 完整实现Mapper接口代理
2. **SQL映射**: 支持注解方式SQL映射
3. **会话管理**: 实现SqlSession模式
4. **自动配置**: 完整的Spring Boot集成

## 📈 学习价值

通过这个简化实现，你可以理解：

1. **MyBatis如何通过动态代理让接口"有了实现"**
2. **SQL映射的本质是什么**
3. **SqlSession的作用和生命周期**
4. **Spring Boot Starter的工作原理**
5. **配置管理和组件注册的过程**

## 💡 已实现功能

### ✅ 企业级功能
1. **事务支持**: ✅ 已集成Spring事务管理
2. **结果缓存**: ✅ 已实现一级/二级缓存
3. **动态SQL**: ✅ 已支持@SelectIf条件判断
4. **异常处理**: ✅ 已完善错误信息和异常链
5. **安全防护**: ✅ 已添加SQL注入防护

### 🔄 未来扩展
1. **监控功能**: SQL执行统计和性能监控
2. **复杂动态SQL**: 支持更多条件标签
3. **插件体系**: 实现拦截器机制
4. **批处理**: 支持批量操作优化
5. **连接池**: 集成高性能连接池

## 🎓 学习价值

这个Mini MyBatis不仅保留了MyBatis最核心的设计思想，现在还包含了企业级应用的关键功能：

### 核心原理学习
- MyBatis的动态代理机制
- SQL映射和会话管理
- Spring Boot自动配置原理

### 企业级功能学习
- 缓存系统的设计与实现
- 事务管理的集成方式
- 安全防护的多层机制
- 异常处理的最佳实践

这是学习和理解MyBatis原理，以及现代ORM框架设计的绝佳材料！ 
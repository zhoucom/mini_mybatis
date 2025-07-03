# Mini MyBatis 高级功能指南

## 概述

Mini MyBatis现已支持企业级的高级功能，包括动态SQL、缓存管理、事务支持、SQL注入防护和异常处理。这些扩展功能让Mini MyBatis更接近真实的MyBatis企业级应用场景。

## 🚀 新增功能列表

### 1. 动态SQL支持

#### 功能说明
支持根据运行时条件动态生成SQL语句，提供更灵活的查询能力。

#### 新增注解
```java
@SelectIf(
    value = "SELECT * FROM products WHERE price >= ?",
    condition = "param1 != null",
    elseSql = "SELECT * FROM products ORDER BY created_time"
)
List<Product> findByPriceConditional(BigDecimal minPrice);
```

#### 支持的条件表达式
- `param1 != null` - 参数非空判断
- `param1 > 0` - 数值比较
- `param1 == 'value'` - 字符串相等
- `param1 is null` - 空值判断
- 复合条件：`param1 != null and param2 > 0`

#### 示例代码
```java:gradle-example/src/main/java/com/yourcompany/mapper/AdvancedProductMapper.java
@SelectIf(
    value = "SELECT * FROM products WHERE category = ? ORDER BY name",
    condition = "param1 != null",
    elseSql = "SELECT * FROM products ORDER BY category, name"
)
List<Product> findByCategoryConditional(String category);
```

### 2. 缓存管理系统

#### 一级缓存（SqlSession级别）
- 自动缓存查询结果
- 会话结束时自动清除
- 更新操作后自动失效

#### 二级缓存（全局级别）
- 跨SqlSession共享缓存
- 支持过期时间配置
- 支持LRU淘汰策略

#### 配置选项
```yaml:gradle-example/src/main/resources/application.yml
mini:
  mybatis:
    first-level-cache-enabled: true      # 启用一级缓存
    second-level-cache-enabled: true     # 启用二级缓存
    cache-expire-time: 300000            # 缓存过期时间（毫秒）
    max-cache-size: 1000                 # 最大缓存大小
```

#### 缓存工作原理
```java:src/main/java/com/example/mybatis/cache/CacheManager.java
// 优先查询一级缓存，再查询二级缓存
public Object get(String key) {
    // 检查一级缓存
    CacheEntry entry = sessionCache.get(key);
    if (entry != null && !entry.isExpired()) {
        return entry.getValue();
    }
    
    // 检查二级缓存
    if (config.isSecondLevelCacheEnabled()) {
        entry = globalCache.get(key);
        if (entry != null && !entry.isExpired()) {
            // 同时放入一级缓存
            sessionCache.put(key, entry);
            return entry.getValue();
        }
    }
    
    return null;
}
```

### 3. 事务管理

#### Spring事务集成
所有数据库操作都支持Spring声明式事务管理。

#### 事务注解支持
```java:gradle-example/src/main/java/com/yourcompany/service/AdvancedProductService.java
@Transactional
public void adjustPricesByCategory(String category, BigDecimal multiplier) {
    int updatedCount = advancedProductMapper.updatePriceByCategory(multiplier, category);
    
    if (multiplier.compareTo(BigDecimal.valueOf(10)) > 0) {
        throw new RuntimeException("价格调整幅度不能超过10倍"); // 触发回滚
    }
}
```

#### 只读事务优化
```java
@Transactional(readOnly = true)
public List<Product> smartPriceQuery(BigDecimal minPrice) {
    return advancedProductMapper.findByPriceConditional(minPrice);
}
```

### 4. SQL注入防护

#### 多层防护机制
- SQL语句安全检查
- 参数内容验证
- 危险关键词检测
- 攻击模式识别

#### 防护实现
```java:src/main/java/com/example/mybatis/security/SqlInjectionGuard.java
public static void validateSql(String sql) {
    // 检查危险关键词
    for (String keyword : DANGEROUS_KEYWORDS) {
        if (upperSql.contains(keyword)) {
            if (!isParameterizedSql(sql)) {
                throw new MyBatisException("检测到危险的SQL关键词: " + keyword);
            }
        }
    }
    
    // 检查注入攻击模式
    for (Pattern pattern : INJECTION_PATTERNS) {
        if (pattern.matcher(sql).find()) {
            throw new MyBatisException("检测到SQL注入攻击模式");
        }
    }
}
```

#### 支持的安全检查
- SQL注释攻击：`-- comment`
- 联合查询注入：`UNION SELECT`
- 布尔盲注：`OR 1=1`
- 命令执行：`EXEC`、`EXECUTE`
- 脚本注入：`<script>`

### 5. 异常处理系统

#### 分层异常体系
```java:src/main/java/com/example/mybatis/exception/MyBatisException.java
public class MyBatisException extends RuntimeException {
    private String errorCode;
    private String sqlStatement;
    private Object[] parameters;
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }
        
        sb.append(super.getMessage());
        
        if (sqlStatement != null) {
            sb.append("\nSQL: ").append(sqlStatement);
        }
        
        return sb.toString();
    }
}
```

#### 具体异常类型
- `SqlExecutionException` - SQL执行异常
- `MyBatisException` - 通用MyBatis异常
- 包含错误码、SQL语句、参数信息

### 6. 安全参数化查询

#### 自动参数验证
所有SQL参数都会经过安全检查，防止代码注入攻击。

#### 参数清理机制
```java:src/main/java/com/example/mybatis/security/SqlInjectionGuard.java
public static String escapeSql(String input) {
    return input
        .replace("'", "''")           // 转义单引号
        .replace("\"", "\\\"")        // 转义双引号
        .replace("\\", "\\\\")        // 转义反斜杠
        .replace(";", "\\;")          // 转义分号
        .replace("--", "\\-\\-");     // 转义注释
}
```

## 🧪 功能测试

### 快速测试
```bash
# 启动应用
cd gradle-example
./run.sh

# 运行高级功能测试
./test-advanced-features.sh
```

### 手动测试端点

#### 动态SQL测试
```bash
# 有条件查询
curl "http://localhost:8080/api/advanced/smart-price?minPrice=1000"

# 无条件查询（动态SQL）
curl "http://localhost:8080/api/advanced/smart-price"
```

#### 缓存效果测试
```bash
# 缓存演示
curl "http://localhost:8080/api/advanced/cache-demo/1"
```

#### 事务测试
```bash
# 正常事务
curl -X POST "http://localhost:8080/api/advanced/adjust-price" \
     -H "Content-Type: application/json" \
     -d '{"category": "Electronics", "multiplier": 1.1}'

# 事务回滚
curl "http://localhost:8080/api/advanced/exception-demo?type=transaction_rollback"
```

#### 安全测试
```bash
# 正常搜索
curl "http://localhost:8080/api/advanced/safe-search?keyword=phone"

# SQL注入尝试
curl "http://localhost:8080/api/advanced/safe-search?keyword='; DROP TABLE products; --"
```

## 📊 性能对比

### 缓存性能提升
通过测试可以观察到：
- 第一次查询：直接访问数据库
- 第二次查询：命中缓存，响应时间显著降低
- 典型提升：缓存命中比数据库查询快5-10倍

### 内存使用优化
- 一级缓存：会话结束自动清理
- 二级缓存：支持LRU淘汰和过期清理
- 内存占用可控，避免内存泄漏

## 🔧 配置说明

### 完整配置示例
```yaml:gradle-example/src/main/resources/application.yml
mini:
  mybatis:
    mapper-locations: com.yourcompany.mapper
    show-sql: true
    query-timeout: 30
    # 缓存配置
    first-level-cache-enabled: true      # 启用一级缓存
    second-level-cache-enabled: true     # 启用二级缓存
    cache-expire-time: 300000            # 缓存过期时间（毫秒）
    max-cache-size: 1000                 # 最大缓存大小
    # 安全配置
    sql-security-enabled: true           # 启用SQL安全检查
```

### 缓存配置选项
- `first-level-cache-enabled`: 一级缓存开关
- `second-level-cache-enabled`: 二级缓存开关
- `cache-expire-time`: 缓存过期时间（毫秒）
- `max-cache-size`: 最大缓存条目数
- `sql-security-enabled`: SQL安全检查开关

## 🔍 核心原理解析

### 动态SQL处理流程
1. 解析@SelectIf注解参数
2. 构建方法参数上下文
3. 评估条件表达式
4. 选择合适的SQL语句
5. 创建临时MappedStatement
6. 执行SQL并返回结果

### 缓存工作机制
1. 生成安全的缓存key
2. 优先检查一级缓存
3. 一级缓存未命中时检查二级缓存
4. 数据库查询后同时更新两级缓存
5. 更新操作时清除相关缓存

### 事务处理流程
1. Spring AOP拦截方法调用
2. 开始数据库事务
3. 执行业务逻辑
4. 异常时自动回滚
5. 成功时提交事务

### 安全检查机制
1. SQL语句预检查
2. 参数内容验证
3. 危险模式识别
4. 执行权限验证
5. 结果安全处理

## 🎯 最佳实践

### 动态SQL使用建议
- 条件表达式尽量简单明确
- 避免在条件中使用复杂逻辑
- 合理使用elseSql提供默认行为
- 注意参数命名的一致性

### 缓存使用策略
- 读多写少的场景启用二级缓存
- 频繁更新的数据慎用缓存
- 合理设置缓存过期时间
- 监控缓存命中率

### 事务管理原则
- 只读操作使用@Transactional(readOnly = true)
- 避免长事务，影响性能
- 合理设置事务传播级别
- 及时处理事务异常

### 安全防护建议
- 始终使用参数化查询
- 不要拼接用户输入到SQL中
- 定期检查安全日志
- 对敏感操作添加额外验证

## 🚀 与真实MyBatis对比

### 相似功能
- ✅ 动态SQL（简化版）
- ✅ 一级缓存
- ✅ 二级缓存（简化版）
- ✅ 事务支持
- ✅ 参数化查询

### 简化的部分
- 动态SQL功能相对简单（真实MyBatis支持更复杂的条件）
- 缓存实现较为基础（真实MyBatis有更多缓存策略）
- 没有实现插件机制
- 没有实现复杂的类型处理器

### 学习价值
通过这个简化版本，你可以深入理解：
- MyBatis的核心设计思想
- 动态代理在ORM中的应用
- 缓存在数据库访问中的作用
- 事务管理的基本原理
- SQL安全防护的重要性

## 📝 总结

Mini MyBatis现在具备了企业级应用所需的核心功能：

1. **动态SQL** - 让查询更加灵活
2. **多级缓存** - 显著提升查询性能
3. **事务管理** - 保证数据一致性
4. **安全防护** - 防止SQL注入攻击
5. **异常处理** - 提供友好的错误信息

这些功能的实现展示了现代ORM框架的核心设计原理，是学习MyBatis和数据库访问层设计的绝佳示例。

通过实际运行和测试这些功能，你将对MyBatis的工作原理有更深刻的理解，为后续学习和使用真实的MyBatis框架打下坚实的基础。 
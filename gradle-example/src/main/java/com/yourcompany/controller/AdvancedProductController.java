package com.yourcompany.controller;

import com.yourcompany.entity.Product;
import com.yourcompany.service.AdvancedProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 高级功能演示控制器
 * 提供REST API来测试Mini MyBatis的扩展功能
 */
@RestController
@RequestMapping("/api/advanced")
public class AdvancedProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedProductController.class);
    
    @Autowired
    private AdvancedProductService advancedProductService;
    
    /**
     * 动态SQL演示 - 智能价格查询
     * GET /api/advanced/smart-price?minPrice=100
     * GET /api/advanced/smart-price (不传参数测试动态SQL)
     */
    @GetMapping("/smart-price")
    public ResponseEntity<Map<String, Object>> smartPriceQuery(
            @RequestParam(required = false) BigDecimal minPrice) {
        
        logger.info("收到智能价格查询请求，参数: {}", minPrice);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Product> products = advancedProductService.smartPriceQuery(minPrice);
            
            response.put("success", true);
            response.put("message", "动态SQL查询成功");
            response.put("data", products);
            response.put("count", products.size());
            response.put("condition", minPrice != null ? "价格过滤" : "全部商品");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("智能价格查询失败", e);
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 缓存演示 - 多次查询同一商品
     * GET /api/advanced/cache-demo/1
     */
    @GetMapping("/cache-demo/{id}")
    public ResponseEntity<Map<String, Object>> cacheDemo(@PathVariable Long id) {
        logger.info("缓存演示 - 商品ID: {}", id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 第一次查询
            long start1 = System.currentTimeMillis();
            Product product1 = advancedProductService.getProductWithCache(id);
            long time1 = System.currentTimeMillis() - start1;
            
            // 第二次查询（应该命中缓存）
            long start2 = System.currentTimeMillis();
            Product product2 = advancedProductService.getProductWithCache(id);
            long time2 = System.currentTimeMillis() - start2;
            
            response.put("success", true);
            response.put("message", "缓存演示完成");
            response.put("product", product1);
            response.put("firstQueryTime", time1 + "ms");
            response.put("secondQueryTime", time2 + "ms");
            response.put("cacheEffect", time1 > time2 ? "缓存生效" : "缓存未生效");
            response.put("speedUp", time1 - time2 + "ms");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("缓存演示失败", e);
            response.put("success", false);
            response.put("message", "演示失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 事务演示 - 批量价格调整
     * POST /api/advanced/adjust-price
     * Body: {"category": "Electronics", "multiplier": 1.1}
     */
    @PostMapping("/adjust-price")
    public ResponseEntity<Map<String, Object>> adjustPrice(
            @RequestBody Map<String, Object> request) {
        
        String category = (String) request.get("category");
        BigDecimal multiplier = new BigDecimal(request.get("multiplier").toString());
        
        logger.info("价格调整请求 - 分类: {}, 乘数: {}", category, multiplier);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            advancedProductService.adjustPricesByCategory(category, multiplier);
            
            response.put("success", true);
            response.put("message", "价格调整成功");
            response.put("category", category);
            response.put("multiplier", multiplier);
            response.put("transactionStatus", "已提交");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("价格调整失败", e);
            response.put("success", false);
            response.put("message", "调整失败: " + e.getMessage());
            response.put("transactionStatus", "已回滚");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 安全性演示 - 安全搜索
     * GET /api/advanced/safe-search?keyword=phone
     * GET /api/advanced/safe-search?keyword='; DROP TABLE products; --
     */
    @GetMapping("/safe-search")
    public ResponseEntity<Map<String, Object>> safeSearch(
            @RequestParam String keyword) {
        
        logger.info("安全搜索请求，关键词: {}", keyword);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Product> products = advancedProductService.safeSearch(keyword);
            
            response.put("success", true);
            response.put("message", "安全搜索完成");
            response.put("keyword", keyword);
            response.put("data", products);
            response.put("count", products.size());
            response.put("securityStatus", "已通过SQL注入检查");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("安全搜索失败", e);
            response.put("success", false);
            response.put("message", "搜索失败: " + e.getMessage());
            response.put("securityStatus", "检测到安全威胁");
            return ResponseEntity.status(400).body(response);
        }
    }
    
    /**
     * 异常处理演示
     * GET /api/advanced/exception-demo?type=sql_injection
     * GET /api/advanced/exception-demo?type=transaction_rollback
     */
    @GetMapping("/exception-demo")
    public ResponseEntity<Map<String, Object>> exceptionDemo(
            @RequestParam String type) {
        
        logger.info("异常处理演示，类型: {}", type);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            switch (type) {
                case "sql_injection":
                    // 尝试SQL注入攻击
                    advancedProductService.safeSearch("'; DROP TABLE products; --");
                    break;
                    
                case "transaction_rollback":
                    // 触发事务回滚
                    advancedProductService.adjustPricesByCategory("Electronics", 
                                                                new BigDecimal("15"));
                    break;
                    
                default:
                    throw new IllegalArgumentException("未知的演示类型: " + type);
            }
            
            response.put("success", true);
            response.put("message", "演示完成");
            
        } catch (Exception e) {
            logger.info("成功捕获预期异常: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "演示异常: " + e.getMessage());
            response.put("exceptionType", e.getClass().getSimpleName());
            response.put("note", "这是预期的演示异常，展示了系统的安全防护能力");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 功能总览
     * GET /api/advanced/features
     */
    @GetMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeatures() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("success", true);
        response.put("message", "Mini MyBatis 高级功能列表");
        
        Map<String, Object> features = new HashMap<>();
        features.put("动态SQL", "支持@SelectIf注解，根据条件动态执行SQL");
        features.put("一级缓存", "SqlSession级别缓存，提升查询性能");
        features.put("二级缓存", "全局共享缓存，跨SqlSession共享结果");
        features.put("事务支持", "集成Spring事务管理，支持声明式事务");
        features.put("SQL注入防护", "自动检测和阻止SQL注入攻击");
        features.put("异常处理", "友好的错误信息和异常链追踪");
        features.put("参数安全", "参数化查询，防止代码注入");
        
        response.put("features", features);
        
        Map<String, String> testEndpoints = new HashMap<>();
        testEndpoints.put("动态SQL测试", "GET /api/advanced/smart-price?minPrice=100");
        testEndpoints.put("缓存测试", "GET /api/advanced/cache-demo/1");
        testEndpoints.put("事务测试", "POST /api/advanced/adjust-price");
        testEndpoints.put("安全测试", "GET /api/advanced/safe-search?keyword=phone");
        testEndpoints.put("异常演示", "GET /api/advanced/exception-demo?type=sql_injection");
        
        response.put("testEndpoints", testEndpoints);
        
        return ResponseEntity.ok(response);
    }
} 
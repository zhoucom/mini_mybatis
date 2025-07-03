package com.yourcompany.controller;

import com.yourcompany.entity.Product;
import com.yourcompany.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品REST API控制器
 * 提供完整的商品管理API接口
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // 允许跨域访问
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // ========== 查询接口 ==========
    
    /**
     * 获取所有商品
     * GET /api/products
     */
    @GetMapping
    public Map<String, Object> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "查询成功");
        result.put("data", products);
        result.put("total", products.size());
        return result;
    }
    
    /**
     * 根据ID获取商品详情
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        Map<String, Object> result = new HashMap<>();
        if (product != null) {
            result.put("success", true);
            result.put("message", "查询成功");
            result.put("data", product);
        } else {
            result.put("success", false);
            result.put("message", "商品不存在");
            result.put("data", null);
        }
        return result;
    }
    
    /**
     * 根据分类查询商品
     * GET /api/products/category/{category}
     */
    @GetMapping("/category/{category}")
    public Map<String, Object> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "查询成功");
        result.put("data", products);
        result.put("total", products.size());
        result.put("category", category);
        return result;
    }
    
    /**
     * 根据价格范围查询商品
     * GET /api/products/price-range?minPrice=100&maxPrice=5000
     */
    @GetMapping("/price-range")
    public Map<String, Object> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "查询成功");
        result.put("data", products);
        result.put("total", products.size());
        result.put("priceRange", minPrice + " - " + maxPrice);
        return result;
    }
    
    /**
     * 搜索商品
     * GET /api/products/search?keyword=MacBook
     */
    @GetMapping("/search")
    public Map<String, Object> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "搜索成功");
        result.put("data", products);
        result.put("total", products.size());
        result.put("keyword", keyword);
        return result;
    }
    
    /**
     * 获取低库存商品
     * GET /api/products/low-stock?threshold=10
     */
    @GetMapping("/low-stock")
    public Map<String, Object> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "查询成功");
        result.put("data", products);
        result.put("total", products.size());
        result.put("threshold", threshold);
        return result;
    }
    
    /**
     * 获取所有商品分类
     * GET /api/products/categories
     */
    @GetMapping("/categories")
    public Map<String, Object> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "查询成功");
        result.put("data", categories);
        result.put("total", categories.size());
        return result;
    }
    
    // ========== 管理接口 ==========
    
    /**
     * 创建商品（基础版本）
     * POST /api/products
     */
    @PostMapping
    public Map<String, Object> createProduct(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        
        boolean success = productService.createProduct(name, price, category);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "商品创建成功" : "商品创建失败");
        return result;
    }
    
    /**
     * 创建商品（完整版本）
     * POST /api/products/full
     */
    @PostMapping("/full")
    public Map<String, Object> createProductFull(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        String description = (String) request.get("description");
        Integer stockQuantity = Integer.valueOf(request.get("stockQuantity").toString());
        
        boolean success = productService.createProductFull(name, price, category, description, stockQuantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "完整商品创建成功" : "完整商品创建失败");
        return result;
    }
    
    /**
     * 更新商品基本信息
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        
        boolean success = productService.updateProduct(id, name, price, category);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "商品更新成功" : "商品更新失败");
        return result;
    }
    
    /**
     * 更新商品完整信息
     * PUT /api/products/{id}/full
     */
    @PutMapping("/{id}/full")
    public Map<String, Object> updateProductFull(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        BigDecimal price = new BigDecimal(request.get("price").toString());
        String category = (String) request.get("category");
        String description = (String) request.get("description");
        Integer stockQuantity = Integer.valueOf(request.get("stockQuantity").toString());
        
        boolean success = productService.updateProductFull(id, name, price, category, description, stockQuantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "商品完整信息更新成功" : "商品完整信息更新失败");
        return result;
    }
    
    /**
     * 更新商品库存
     * PUT /api/products/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public Map<String, Object> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer stockQuantity = Integer.valueOf(request.get("stockQuantity").toString());
        
        boolean success = productService.updateStock(id, stockQuantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "库存更新成功" : "库存更新失败");
        return result;
    }
    
    /**
     * 减少库存
     * POST /api/products/{id}/decrease-stock
     */
    @PostMapping("/{id}/decrease-stock")
    public Map<String, Object> decreaseStock(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        boolean success = productService.decreaseStock(id, quantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "库存减少成功" : "库存减少失败，可能库存不足");
        return result;
    }
    
    /**
     * 增加库存
     * POST /api/products/{id}/increase-stock
     */
    @PostMapping("/{id}/increase-stock")
    public Map<String, Object> increaseStock(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        boolean success = productService.increaseStock(id, quantity);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "库存增加成功" : "库存增加失败");
        return result;
    }
    
    /**
     * 删除商品
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "商品删除成功" : "商品删除失败");
        return result;
    }
    
    /**
     * 清理缺货商品
     * DELETE /api/products/cleanup/out-of-stock
     */
    @DeleteMapping("/cleanup/out-of-stock")
    public Map<String, Object> cleanOutOfStockProducts() {
        int deletedCount = productService.cleanOutOfStockProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缺货商品清理完成");
        result.put("deletedCount", deletedCount);
        return result;
    }
    
    // ========== 统计接口 ==========
    
    /**
     * 获取商品统计信息
     * GET /api/products/stats
     */
    @GetMapping("/stats")
    public Map<String, Object> getProductStats() {
        Long totalCount = productService.getTotalProductCount();
        Long totalStock = productService.getTotalStockCount();
        ProductService.PriceStats priceStats = productService.getPriceStats();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "统计信息获取成功");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", totalCount);
        stats.put("totalStock", totalStock);
        stats.put("maxPrice", priceStats.getMaxPrice());
        stats.put("minPrice", priceStats.getMinPrice());
        stats.put("averagePrice", priceStats.getAveragePrice());
        
        result.put("data", stats);
        return result;
    }
    
    /**
     * 获取分类统计信息
     * GET /api/products/stats/category/{category}
     */
    @GetMapping("/stats/category/{category}")
    public Map<String, Object> getCategoryStats(@PathVariable String category) {
        Long count = productService.getCategoryProductCount(category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "分类统计信息获取成功");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("category", category);
        stats.put("productCount", count);
        
        result.put("data", stats);
        return result;
    }
    
    // ========== 健康检查接口 ==========
    
    /**
     * 健康检查
     * GET /api/products/health
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Mini MyBatis Gradle示例运行正常");
        result.put("timestamp", System.currentTimeMillis());
        
        // 简单的功能检查
        try {
            Long count = productService.getTotalProductCount();
            result.put("productCount", count);
            result.put("status", "healthy");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
} 
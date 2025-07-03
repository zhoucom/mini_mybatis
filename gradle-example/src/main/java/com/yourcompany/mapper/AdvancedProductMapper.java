package com.yourcompany.mapper;

import com.yourcompany.entity.Product;
import com.example.mybatis.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 高级功能演示Mapper
 * 展示动态SQL、缓存、事务等新增功能
 */
@MyBatisMapper
public interface AdvancedProductMapper {
    
    // ========== 动态SQL演示 ==========
    
    /**
     * 条件查询商品 - 动态SQL示例
     * 当价格参数不为null时才添加价格条件
     */
    @SelectIf(
        value = "SELECT id, name, price, category, description, stock_quantity " +
                "FROM products WHERE price >= ? ORDER BY price ASC",
        condition = "param1 != null",
        elseSql = "SELECT id, name, price, category, description, stock_quantity " +
                  "FROM products ORDER BY created_time DESC"
    )
    List<Product> findByPriceConditional(BigDecimal minPrice);
    
    /**
     * 根据库存状态查询
     * 当库存阈值大于0时查询低库存商品，否则查询所有商品
     */
    @SelectIf(
        value = "SELECT id, name, price, category, description, stock_quantity " +
                "FROM products WHERE stock_quantity < ? ORDER BY stock_quantity ASC",
        condition = "param1 > 0",
        elseSql = "SELECT id, name, price, category, description, stock_quantity " +
                  "FROM products ORDER BY id"
    )
    List<Product> findByStockConditional(Integer threshold);
    
    /**
     * 分类查询 - 支持空值处理
     */
    @SelectIf(
        value = "SELECT id, name, price, category, description, stock_quantity " +
                "FROM products WHERE category = ? ORDER BY name",
        condition = "param1 != null",
        elseSql = "SELECT id, name, price, category, description, stock_quantity " +
                  "FROM products ORDER BY category, name"
    )
    List<Product> findByCategoryConditional(String category);
    
    // ========== 缓存演示 ==========
    
    /**
     * 高频查询 - 会被自动缓存
     * 多次调用相同参数时会命中缓存
     */
    @Select("SELECT id, name, price, category, description, stock_quantity " +
            "FROM products WHERE id = ?")
    Product findByIdCached(Long id);
    
    /**
     * 热门商品查询 - 缓存示例
     */
    @Select("SELECT id, name, price, category, description, stock_quantity " +
            "FROM products WHERE price > 1000 ORDER BY price DESC LIMIT 10")
    List<Product> findExpensiveProducts();
    
    // ========== 事务演示 ==========
    
    /**
     * 批量更新价格 - 事务操作
     */
    @Update("UPDATE products SET price = price * ? WHERE category = ?")
    int updatePriceByCategory(BigDecimal multiplier, String category);
    
    /**
     * 清理过期库存 - 事务操作
     */
    @Delete("DELETE FROM products WHERE stock_quantity = 0 AND created_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int cleanExpiredProducts();
    
    // ========== 安全性演示 ==========
    
    /**
     * 安全的用户输入查询
     * 参数会经过SQL注入检查
     */
    @Select("SELECT id, name, price, category, description, stock_quantity " +
            "FROM products WHERE name LIKE ? OR description LIKE ?")
    List<Product> searchProductsSafely(String namePattern, String descPattern);
    
    /**
     * 参数化查询示例
     */
    @Select("SELECT id, name, price, category, description, stock_quantity " +
            "FROM products WHERE category IN (?, ?, ?) ORDER BY price")
    List<Product> findByMultipleCategories(String category1, String category2, String category3);
    
    // ========== 复杂查询演示 ==========
    
    /**
     * 聚合查询 - 统计信息
     */
    @Select("SELECT category, COUNT(*) as product_count, AVG(price) as avg_price, " +
            "SUM(stock_quantity) as total_stock FROM products GROUP BY category")
    List<Object> getCategoryStatistics();
    
    /**
     * 关联查询模拟 - 商品和库存信息
     */
    @Select("SELECT p.id, p.name, p.price, p.category, p.stock_quantity, " +
            "CASE WHEN p.stock_quantity > 50 THEN '充足' " +
            "     WHEN p.stock_quantity > 10 THEN '正常' " +
            "     WHEN p.stock_quantity > 0 THEN '不足' " +
            "     ELSE '缺货' END as stock_status " +
            "FROM products p WHERE p.price BETWEEN ? AND ?")
    List<Object> getProductsWithStockStatus(BigDecimal minPrice, BigDecimal maxPrice);
} 
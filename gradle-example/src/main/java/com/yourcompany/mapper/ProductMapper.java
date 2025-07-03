package com.yourcompany.mapper;

import com.yourcompany.entity.Product;
import com.example.mybatis.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper接口
 * 演示Mini MyBatis的各种SQL操作功能
 */
@MyBatisMapper
public interface ProductMapper {
    
    // ========== 查询操作 ==========
    
    /**
     * 根据ID查询商品详情
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products WHERE id = ?")
    Product findById(Long id);
    
    /**
     * 查询所有商品
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products ORDER BY created_time DESC")
    List<Product> findAll();
    
    /**
     * 根据分类查询商品
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products WHERE category = ? ORDER BY price ASC")
    List<Product> findByCategory(String category);
    
    /**
     * 根据价格范围查询商品
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products WHERE price BETWEEN ? AND ? ORDER BY price ASC")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 根据名称模糊查询商品
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products WHERE name LIKE ? ORDER BY name ASC")
    List<Product> findByNameLike(String namePattern);
    
    /**
     * 查询库存低于指定数量的商品
     */
    @Select("SELECT id, name, price, category, description, stock_quantity, created_time, updated_time " +
            "FROM products WHERE stock_quantity < ? ORDER BY stock_quantity ASC")
    List<Product> findLowStockProducts(Integer threshold);
    
    /**
     * 统计商品总数
     */
    @Select("SELECT COUNT(*) FROM products")
    Long countAll();
    
    /**
     * 统计指定分类的商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE category = ?")
    Long countByCategory(String category);
    
    /**
     * 获取所有商品分类
     */
    @Select("SELECT DISTINCT category FROM products ORDER BY category")
    List<String> findAllCategories();
    
    // ========== 插入操作 ==========
    
    /**
     * 插入新商品（基础版本）
     */
    @Insert("INSERT INTO products(name, price, category) VALUES(?, ?, ?)")
    int insert(String name, BigDecimal price, String category);
    
    /**
     * 插入新商品（完整版本）
     */
    @Insert("INSERT INTO products(name, price, category, description, stock_quantity) " +
            "VALUES(?, ?, ?, ?, ?)")
    int insertFull(String name, BigDecimal price, String category, String description, Integer stockQuantity);
    
    // ========== 更新操作 ==========
    
    /**
     * 更新商品基本信息
     */
    @Update("UPDATE products SET name = ?, price = ?, category = ? WHERE id = ?")
    int updateBasicInfo(String name, BigDecimal price, String category, Long id);
    
    /**
     * 更新商品完整信息
     */
    @Update("UPDATE products SET name = ?, price = ?, category = ?, description = ?, stock_quantity = ? " +
            "WHERE id = ?")
    int updateFullInfo(String name, BigDecimal price, String category, String description, 
                      Integer stockQuantity, Long id);
    
    /**
     * 更新商品库存
     */
    @Update("UPDATE products SET stock_quantity = ? WHERE id = ?")
    int updateStock(Integer stockQuantity, Long id);
    
    /**
     * 减少商品库存
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?")
    int decreaseStock(Integer quantity, Long id, Integer requiredStock);
    
    /**
     * 增加商品库存
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?")
    int increaseStock(Integer quantity, Long id);
    
    // ========== 删除操作 ==========
    
    /**
     * 根据ID删除商品
     */
    @Delete("DELETE FROM products WHERE id = ?")
    int deleteById(Long id);
    
    /**
     * 根据分类删除商品
     */
    @Delete("DELETE FROM products WHERE category = ?")
    int deleteByCategory(String category);
    
    /**
     * 删除库存为0的商品
     */
    @Delete("DELETE FROM products WHERE stock_quantity = 0")
    int deleteOutOfStockProducts();
    
    // ========== 统计分析 ==========
    
    /**
     * 获取最高价格
     */
    @Select("SELECT MAX(price) FROM products")
    BigDecimal getMaxPrice();
    
    /**
     * 获取最低价格
     */
    @Select("SELECT MIN(price) FROM products")
    BigDecimal getMinPrice();
    
    /**
     * 获取平均价格
     */
    @Select("SELECT AVG(price) FROM products")
    BigDecimal getAveragePrice();
    
    /**
     * 获取总库存数量
     */
    @Select("SELECT SUM(stock_quantity) FROM products")
    Long getTotalStock();
    
    /**
     * 获取分类统计信息
     */
    @Select("SELECT category, COUNT(*) as count, AVG(price) as avg_price, SUM(stock_quantity) as total_stock " +
            "FROM products GROUP BY category ORDER BY count DESC")
    List<Object> getCategoryStats();
} 
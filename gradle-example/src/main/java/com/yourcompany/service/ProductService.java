package com.yourcompany.service;

import com.yourcompany.entity.Product;
import com.yourcompany.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务类
 * 封装商品相关的业务逻辑
 */
@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    @Qualifier("productMapper")
    private ProductMapper productMapper;
    
    // ========== 查询服务 ==========
    
    /**
     * 获取商品详情
     */
    public Product getProductById(Long id) {
        logger.info("查询商品详情，ID: {}", id);
        return productMapper.findById(id);
    }
    
    /**
     * 获取所有商品
     */
    public List<Product> getAllProducts() {
        logger.info("查询所有商品");
        return productMapper.findAll();
    }
    
    /**
     * 根据分类获取商品
     */
    public List<Product> getProductsByCategory(String category) {
        logger.info("根据分类查询商品，分类: {}", category);
        return productMapper.findByCategory(category);
    }
    
    /**
     * 根据价格范围查询商品
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("根据价格范围查询商品，价格区间: {} - {}", minPrice, maxPrice);
        return productMapper.findByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * 搜索商品
     */
    public List<Product> searchProducts(String keyword) {
        logger.info("搜索商品，关键词: {}", keyword);
        String pattern = "%" + keyword + "%";
        return productMapper.findByNameLike(pattern);
    }
    
    /**
     * 获取低库存商品
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        logger.info("查询低库存商品，阈值: {}", threshold);
        return productMapper.findLowStockProducts(threshold);
    }
    
    /**
     * 获取所有商品分类
     */
    public List<String> getAllCategories() {
        logger.info("获取所有商品分类");
        return productMapper.findAllCategories();
    }
    
    // ========== 管理服务 ==========
    
    /**
     * 创建商品（基础版本）
     */
    public boolean createProduct(String name, BigDecimal price, String category) {
        logger.info("创建商品: {}, 价格: {}, 分类: {}", name, price, category);
        int result = productMapper.insert(name, price, category);
        boolean success = result > 0;
        logger.info("商品创建{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 创建商品（完整版本）
     */
    public boolean createProductFull(String name, BigDecimal price, String category, 
                                   String description, Integer stockQuantity) {
        logger.info("创建完整商品信息: {}", name);
        int result = productMapper.insertFull(name, price, category, description, stockQuantity);
        boolean success = result > 0;
        logger.info("完整商品创建{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 更新商品基本信息
     */
    public boolean updateProduct(Long id, String name, BigDecimal price, String category) {
        logger.info("更新商品基本信息，ID: {}", id);
        int result = productMapper.updateBasicInfo(name, price, category, id);
        boolean success = result > 0;
        logger.info("商品更新{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 更新商品完整信息
     */
    public boolean updateProductFull(Long id, String name, BigDecimal price, String category,
                                   String description, Integer stockQuantity) {
        logger.info("更新商品完整信息，ID: {}", id);
        int result = productMapper.updateFullInfo(name, price, category, description, stockQuantity, id);
        boolean success = result > 0;
        logger.info("商品完整信息更新{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 更新库存
     */
    public boolean updateStock(Long id, Integer stockQuantity) {
        logger.info("更新商品库存，ID: {}, 新库存: {}", id, stockQuantity);
        int result = productMapper.updateStock(stockQuantity, id);
        boolean success = result > 0;
        logger.info("库存更新{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 减少库存（用于订单处理）
     */
    public boolean decreaseStock(Long id, Integer quantity) {
        logger.info("减少商品库存，ID: {}, 减少数量: {}", id, quantity);
        int result = productMapper.decreaseStock(quantity, id, quantity);
        boolean success = result > 0;
        if (success) {
            logger.info("库存减少成功");
        } else {
            logger.warn("库存减少失败，可能库存不足");
        }
        return success;
    }
    
    /**
     * 增加库存（用于补货）
     */
    public boolean increaseStock(Long id, Integer quantity) {
        logger.info("增加商品库存，ID: {}, 增加数量: {}", id, quantity);
        int result = productMapper.increaseStock(quantity, id);
        boolean success = result > 0;
        logger.info("库存增加{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 删除商品
     */
    public boolean deleteProduct(Long id) {
        logger.info("删除商品，ID: {}", id);
        int result = productMapper.deleteById(id);
        boolean success = result > 0;
        logger.info("商品删除{}", success ? "成功" : "失败");
        return success;
    }
    
    /**
     * 清理缺货商品
     */
    public int cleanOutOfStockProducts() {
        logger.info("清理缺货商品");
        int deletedCount = productMapper.deleteOutOfStockProducts();
        logger.info("已清理 {} 个缺货商品", deletedCount);
        return deletedCount;
    }
    
    // ========== 统计服务 ==========
    
    /**
     * 获取商品总数
     */
    public Long getTotalProductCount() {
        return productMapper.countAll();
    }
    
    /**
     * 获取分类商品数量
     */
    public Long getCategoryProductCount(String category) {
        return productMapper.countByCategory(category);
    }
    
    /**
     * 获取价格统计信息
     */
    public PriceStats getPriceStats() {
        BigDecimal maxPrice = productMapper.getMaxPrice();
        BigDecimal minPrice = productMapper.getMinPrice();
        BigDecimal avgPrice = productMapper.getAveragePrice();
        
        PriceStats stats = new PriceStats();
        stats.setMaxPrice(maxPrice);
        stats.setMinPrice(minPrice);
        stats.setAveragePrice(avgPrice);
        
        return stats;
    }
    
    /**
     * 获取库存统计
     */
    public Long getTotalStockCount() {
        return productMapper.getTotalStock();
    }
    
    // 价格统计内部类
    public static class PriceStats {
        private BigDecimal maxPrice;
        private BigDecimal minPrice;
        private BigDecimal averagePrice;
        
        // Getters and Setters
        public BigDecimal getMaxPrice() { return maxPrice; }
        public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
        
        public BigDecimal getMinPrice() { return minPrice; }
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
        
        public BigDecimal getAveragePrice() { return averagePrice; }
        public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }
        
        @Override
        public String toString() {
            return "PriceStats{" +
                    "maxPrice=" + maxPrice +
                    ", minPrice=" + minPrice +
                    ", averagePrice=" + averagePrice +
                    '}';
        }
    }
} 
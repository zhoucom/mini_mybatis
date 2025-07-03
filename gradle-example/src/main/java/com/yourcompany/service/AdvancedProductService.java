package com.yourcompany.service;

import com.yourcompany.entity.Product;
import com.yourcompany.mapper.AdvancedProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 高级功能演示服务类
 * 展示事务管理、缓存优化、异常处理等企业级特性
 */
@Service
@Transactional
public class AdvancedProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedProductService.class);
    
    @Autowired
    private AdvancedProductMapper advancedProductMapper;
    
    /**
     * 智能价格查询 - 动态SQL演示
     */
    @Transactional(readOnly = true)
    public List<Product> smartPriceQuery(BigDecimal minPrice) {
        logger.info("执行智能价格查询，参数: {}", minPrice);
        
        try {
            List<Product> products = advancedProductMapper.findByPriceConditional(minPrice);
            logger.info("查询到 {} 个商品", products.size());
            return products;
        } catch (Exception e) {
            logger.error("智能价格查询失败", e);
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 高频商品查询 - 缓存演示
     */
    @Transactional(readOnly = true)
    public Product getProductWithCache(Long id) {
        logger.info("获取商品信息（缓存优化）: {}", id);
        return advancedProductMapper.findByIdCached(id);
    }
    
    /**
     * 批量价格调整 - 事务演示
     */
    @Transactional
    public void adjustPricesByCategory(String category, BigDecimal multiplier) {
        logger.info("批量调整分类 {} 的价格，乘数: {}", category, multiplier);
        
        try {
            int updatedCount = advancedProductMapper.updatePriceByCategory(multiplier, category);
            logger.info("成功更新 {} 个商品的价格", updatedCount);
            
            if (multiplier.compareTo(BigDecimal.valueOf(10)) > 0) {
                logger.error("价格调整幅度过大，回滚事务");
                throw new RuntimeException("价格调整幅度不能超过10倍");
            }
        } catch (Exception e) {
            logger.error("价格调整失败，事务将回滚", e);
            throw e;
        }
    }
    
    /**
     * 安全搜索 - SQL注入防护演示
     */
    @Transactional(readOnly = true)
    public List<Product> safeSearch(String keyword) {
        logger.info("执行安全搜索: {}", keyword);
        
        try {
            String pattern = "%" + keyword + "%";
            return advancedProductMapper.searchProductsSafely(pattern, pattern);
        } catch (Exception e) {
            logger.error("搜索失败，可能包含非法字符", e);
            throw new RuntimeException("搜索失败: " + e.getMessage(), e);
        }
    }
} 
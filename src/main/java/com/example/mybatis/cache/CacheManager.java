package com.example.mybatis.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 缓存管理器
 * 实现一级缓存（SqlSession级别）和二级缓存（全局级别）
 */
public class CacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);
    
    /**
     * 二级缓存 - 全局共享
     */
    private static final Map<String, CacheEntry> globalCache = new ConcurrentHashMap<>();
    
    /**
     * 缓存锁
     */
    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    
    /**
     * 一级缓存 - SqlSession级别
     */
    private final Map<String, CacheEntry> sessionCache = new ConcurrentHashMap<>();
    
    /**
     * 缓存配置
     */
    private final CacheConfig config;
    
    public CacheManager(CacheConfig config) {
        this.config = config;
    }
    
    /**
     * 从缓存获取数据
     * 优先查询一级缓存，再查询二级缓存
     */
    public Object get(String key) {
        // 检查一级缓存
        CacheEntry entry = sessionCache.get(key);
        if (entry != null && !entry.isExpired()) {
            logger.debug("一级缓存命中: {}", key);
            return entry.getValue();
        }
        
        // 检查二级缓存
        // 优先从二级缓存中获取，如果命中则返回并将其放入一级缓存
        if (config.isSecondLevelCacheEnabled()) {
            cacheLock.readLock().lock();
            try {
                entry = globalCache.get(key);
                if (entry != null && !entry.isExpired()) {
                    logger.debug("二级缓存命中: {}", key);
                    // 同时放入一级缓存，以便下次使用
                    sessionCache.put(key, entry);
                    return entry.getValue();
                }
            } finally {
                cacheLock.readLock().unlock();
            }
        }
        
        logger.debug("缓存未命中: {}", key);
        return null;
    }
    
    /**
     * 向缓存存入数据
     */
    public void put(String key, Object value) {
        if (value == null) {
            return;
        }
        
        CacheEntry entry = new CacheEntry(value, config.getExpireTime());
        
        // 存入一级缓存
        sessionCache.put(key, entry);
        logger.debug("数据存入一级缓存: {}", key);
        
        // 存入二级缓存
        if (config.isSecondLevelCacheEnabled()) {
            cacheLock.writeLock().lock();
            try {
                globalCache.put(key, entry);
                logger.debug("数据存入二级缓存: {}", key);
                
                // 检查缓存大小限制
                if (globalCache.size() > config.getMaxSize()) {
                    evictExpiredEntries();
                }
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }
    
    /**
     * 清除一级缓存
     */
    public void clearSessionCache() {
        sessionCache.clear();
        logger.debug("一级缓存已清除");
    }
    
    /**
     * 清除二级缓存
     */
    public static void clearGlobalCache() {
        cacheLock.writeLock().lock();
        try {
            globalCache.clear();
            logger.debug("二级缓存已清除");
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    /**
     * 移除指定key的缓存
     */
    public void remove(String key) {
        sessionCache.remove(key);
        
        if (config.isSecondLevelCacheEnabled()) {
            cacheLock.writeLock().lock();
            try {
                globalCache.remove(key);
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
        
        logger.debug("缓存项已移除: {}", key);
    }
    
    /**
     * 清理过期的缓存项
     */
    private void evictExpiredEntries() {
        globalCache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                logger.debug("清理过期缓存: {}", entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStats getStats() {
        return new CacheStats(
            sessionCache.size(),
            globalCache.size(),
            config.isSecondLevelCacheEnabled()
        );
    }
    
    /**
     * 缓存项
     */
    private static class CacheEntry {
        private final Object value;
        private final long expireTime;
        
        public CacheEntry(Object value, long ttl) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + ttl;
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
    
    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private final int sessionCacheSize;
        private final int globalCacheSize;
        private final boolean secondLevelEnabled;
        
        public CacheStats(int sessionCacheSize, int globalCacheSize, boolean secondLevelEnabled) {
            this.sessionCacheSize = sessionCacheSize;
            this.globalCacheSize = globalCacheSize;
            this.secondLevelEnabled = secondLevelEnabled;
        }
        
        public int getSessionCacheSize() { return sessionCacheSize; }
        public int getGlobalCacheSize() { return globalCacheSize; }
        public boolean isSecondLevelEnabled() { return secondLevelEnabled; }
        
        @Override
        public String toString() {
            return "CacheStats{" +
                    "sessionCache=" + sessionCacheSize +
                    ", globalCache=" + globalCacheSize +
                    ", secondLevel=" + secondLevelEnabled +
                    '}';
        }
    }
} 
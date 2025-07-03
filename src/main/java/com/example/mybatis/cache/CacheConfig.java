package com.example.mybatis.cache;

/**
 * 缓存配置类
 */
public class CacheConfig {
    
    /**
     * 是否启用一级缓存
     */
    private boolean firstLevelCacheEnabled = true;
    
    /**
     * 是否启用二级缓存
     */
    private boolean secondLevelCacheEnabled = false;
    
    /**
     * 缓存过期时间（毫秒）
     */
    private long expireTime = 300000; // 5分钟
    
    /**
     * 最大缓存大小
     */
    private int maxSize = 1000;
    
    /**
     * 缓存命中率统计
     */
    private boolean enableStats = true;
    
    public CacheConfig() {
    }
    
    public CacheConfig(boolean secondLevelCacheEnabled, long expireTime, int maxSize) {
        this.secondLevelCacheEnabled = secondLevelCacheEnabled;
        this.expireTime = expireTime;
        this.maxSize = maxSize;
    }
    
    // Getters and Setters
    public boolean isFirstLevelCacheEnabled() {
        return firstLevelCacheEnabled;
    }
    
    public void setFirstLevelCacheEnabled(boolean firstLevelCacheEnabled) {
        this.firstLevelCacheEnabled = firstLevelCacheEnabled;
    }
    
    public boolean isSecondLevelCacheEnabled() {
        return secondLevelCacheEnabled;
    }
    
    public void setSecondLevelCacheEnabled(boolean secondLevelCacheEnabled) {
        this.secondLevelCacheEnabled = secondLevelCacheEnabled;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public boolean isEnableStats() {
        return enableStats;
    }
    
    public void setEnableStats(boolean enableStats) {
        this.enableStats = enableStats;
    }
    
    @Override
    public String toString() {
        return "CacheConfig{" +
                "firstLevelCacheEnabled=" + firstLevelCacheEnabled +
                ", secondLevelCacheEnabled=" + secondLevelCacheEnabled +
                ", expireTime=" + expireTime +
                ", maxSize=" + maxSize +
                ", enableStats=" + enableStats +
                '}';
    }
} 
package com.example.mybatis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis配置属性
 * 用于外部化配置MyBatis相关参数
 */
@ConfigurationProperties(prefix = "mini.mybatis")
public class MyBatisProperties {
    
    /**
     * Mapper接口扫描路径
     * 多个路径用逗号分隔
     */
    private String mapperLocations = "com.example.mybatis.mapper,com.yourcompany.mapper";
    
    /**
     * SQL执行超时时间（秒）
     */
    private int queryTimeout = 30;
    
    /**
     * 是否开启SQL日志
     */
    private boolean showSql = true;
    
    /**
     * 是否启用一级缓存
     */
    private boolean firstLevelCacheEnabled = true;
    
    /**
     * 是否启用二级缓存
     */
    private boolean secondLevelCacheEnabled = false;
    
    /**
     * 缓存过期时间（秒）
     */
    private long cacheExpireTime = 1800;
    
    /**
     * 最大缓存大小
     */
    private int maxCacheSize = 1000;
    
    /**
     * 是否启用SQL安全检查
     */
    private boolean sqlSecurityEnabled = true;
    
    public String getMapperLocations() {
        return mapperLocations;
    }
    
    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }
    
    public int getQueryTimeout() {
        return queryTimeout;
    }
    
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
    
    public boolean isShowSql() {
        return showSql;
    }
    
    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }
    
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
    
    public long getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public int getMaxCacheSize() {
        return maxCacheSize;
    }
    
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    public boolean isSqlSecurityEnabled() {
        return sqlSecurityEnabled;
    }
    
    public void setSqlSecurityEnabled(boolean sqlSecurityEnabled) {
        this.sqlSecurityEnabled = sqlSecurityEnabled;
    }
    
    @Override
    public String toString() {
        return "MyBatisProperties{" +
                "mapperLocations='" + mapperLocations + '\'' +
                ", queryTimeout=" + queryTimeout +
                ", showSql=" + showSql +
                ", firstLevelCacheEnabled=" + firstLevelCacheEnabled +
                ", secondLevelCacheEnabled=" + secondLevelCacheEnabled +
                ", cacheExpireTime=" + cacheExpireTime +
                ", maxCacheSize=" + maxCacheSize +
                ", sqlSecurityEnabled=" + sqlSecurityEnabled +
                '}';
    }
} 
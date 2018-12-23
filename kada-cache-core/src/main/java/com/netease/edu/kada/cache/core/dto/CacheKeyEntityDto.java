package com.netease.edu.kada.cache.core.dto;

import javax.persistence.*;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:21.
 */
public class CacheKeyEntityDto {
    private Long id;
    //项目名称（必须唯一）
    private String appName;
    private CacheEntityDto cacheEntity;
    //缓存key
    private String cacheKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CacheEntityDto getCacheEntity() {
        return cacheEntity;
    }

    public void setCacheEntity(CacheEntityDto cacheEntity) {
        this.cacheEntity = cacheEntity;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String key) {
        this.cacheKey = key;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

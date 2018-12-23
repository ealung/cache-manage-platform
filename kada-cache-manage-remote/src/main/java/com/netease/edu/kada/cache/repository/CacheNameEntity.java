package com.netease.edu.kada.cache.repository;

import javax.persistence.*;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:47.
 */
@Entity
public class CacheNameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //项目名称（必须唯一）
    private String appName;
    @JoinColumn(name = "cache_id")
    @ManyToOne
    private CacheEntity cacheEntity;
    private String cacheName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CacheEntity getCacheEntity() {
        return cacheEntity;
    }

    public void setCacheEntity(CacheEntity cacheEntity) {
        this.cacheEntity = cacheEntity;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

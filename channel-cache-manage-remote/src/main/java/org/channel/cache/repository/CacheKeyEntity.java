package org.channel.cache.repository;

import javax.persistence.*;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:21.
 */
@Entity
public class CacheKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //项目名称（必须唯一）
    private String appName;
    @JoinColumn(name = "cache_id")
    @ManyToOne
    private CacheEntity cacheEntity;
    //缓存key
    private String cacheKey;

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

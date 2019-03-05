package org.channel.cache.core.dto;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:47.
 */
public class CacheNameDto {
    private Long id;
    private String appName;
    private String cacheName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

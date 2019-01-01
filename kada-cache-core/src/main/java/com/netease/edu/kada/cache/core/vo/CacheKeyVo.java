package com.netease.edu.kada.cache.core.vo;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:21.
 */
public class CacheKeyVo {
    private Long id;
    //项目名称（必须唯一）
    private String appName;
    //缓存key
    private String cacheKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

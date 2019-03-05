package org.channel.cache.core.dto;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/10/16 17:18.
 */
public class CacheDto {
    private Long id;
    //项目名称（必须唯一）
    private String appName;
    //类名称
    private String className;
    //方法名称
    private String methodName;
    //缓存配置的key
    private String cacheConfigKey;
    //缓存操作类型
    private String cacheOperation;

    private Collection<CacheKeyDto> cacheKeyDtos;
    private Collection<CacheNameDto> cacheNameDtos;

    public Collection<CacheKeyDto> getCacheKeyDtos() {
        return cacheKeyDtos;
    }

    public void setCacheKeyDtos(Collection<CacheKeyDto> cacheKeyDtos) {
        this.cacheKeyDtos = cacheKeyDtos;
    }

    public Collection<CacheNameDto> getCacheNameDtos() {
        return cacheNameDtos;
    }

    public void setCacheNameDtos(Collection<CacheNameDto> cacheNameDtos) {
        this.cacheNameDtos = cacheNameDtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCacheConfigKey() {
        return cacheConfigKey;
    }

    public void setCacheConfigKey(String cacheConfigKey) {
        this.cacheConfigKey = cacheConfigKey;
    }

    public String getCacheOperation() {
        return cacheOperation;
    }

    public void setCacheOperation(String cacheOperation) {
        this.cacheOperation = cacheOperation;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

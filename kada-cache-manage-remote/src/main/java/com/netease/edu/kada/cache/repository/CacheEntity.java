package com.netease.edu.kada.cache.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author zhangchanglu<hzzhangchanglu @ corp.netease.com>
 * @since 2018/10/16 17:18.
 */
@Entity
public class CacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

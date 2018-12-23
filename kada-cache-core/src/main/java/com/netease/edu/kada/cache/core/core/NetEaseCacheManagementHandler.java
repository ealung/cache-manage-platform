package com.netease.edu.kada.cache.core.core;

import com.netease.edu.kada.cache.core.core.duplicate.CacheAspectSupport;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.stereotype.Component;

/**
 * @author zhangchanglu
 * @since 2018/09/26 17:05.
 */
@Component
@Slf4j
public class NetEaseCacheManagementHandler implements NetEaseCacheHandler {
    private ProjectCacheInvoke projectCacheInvoke;
    @Value("${cache.app.name:cacheApp}")
    private String appName;

    @Override
    public void setProjectCacheInvoke(ProjectCacheInvoke projectCacheInvoke) {
        this.projectCacheInvoke = projectCacheInvoke;
    }

    @Override
    public void afterClear(Cache cache, CacheAspectSupport.CacheOperationContext cacheOperationContext) {
        try {
            projectCacheInvoke.afterClear(cache.getName(), appName);
        } catch (Exception e) {
            log.warn("projectCacheInvoke.afterClear fail", e);
        }
    }

    @Override
    public void afterCacheGet(Cache cache, Object key, CacheAspectSupport.CacheOperationContext cacheOperationContext) {
        try {
            projectCacheInvoke.afterCacheGet(cache.getName(), key, appName);
        } catch (Exception e) {
            log.warn("projectCacheInvoke.afterCacheGet fail", e);
        }
    }

    @Override
    public void afterCacheEvict(Cache cache, Object key, CacheAspectSupport.CacheOperationContext cacheOperationContext) {
        try {
            projectCacheInvoke.afterCacheEvict(cache.getName(), key, appName);
        } catch (Exception e) {
            log.warn("projectCacheInvoke.afterCacheEvict fail", e);
        }
    }

    @Override
    public void afterCachePut(Cache cache, Object key, Object result, CacheAspectSupport.CacheOperationContext cacheOperationContext) {
        try {
            String className = cacheOperationContext.getTarget().getClass().getName();
            String methodName = cacheOperationContext.getMethod().getName();
            CacheOperation cacheOperation = cacheOperationContext.getOperation();
            projectCacheInvoke.afterCachePut(cache.getName(), key, className, methodName, cacheOperation.getKey(), appName);
        } catch (Exception e) {
            log.warn("projectCacheInvoke.afterCachePut fail", e);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

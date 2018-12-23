package com.netease.edu.kada.cache.core.core;

import com.netease.edu.kada.cache.core.core.duplicate.CacheAspectSupport;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/**
 * @author zhangchanglu
 * @since 2018/09/26 16:21.
 */
public interface NetEaseCacheHandler extends Ordered {

    void setProjectCacheInvoke(ProjectCacheInvoke projectCacheInvoke);

    void afterClear(Cache cache, CacheAspectSupport.CacheOperationContext cacheOperationContext);

    void afterCacheGet(Cache cache, Object key, CacheAspectSupport.CacheOperationContext cacheOperationContext);

    void afterCacheEvict(Cache cache, Object key, CacheAspectSupport.CacheOperationContext cacheOperationContext);

    void afterCachePut(Cache cache, Object key, @Nullable Object result, CacheAspectSupport.CacheOperationContext cacheOperationContext);
}

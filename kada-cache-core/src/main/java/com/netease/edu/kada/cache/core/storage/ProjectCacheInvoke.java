package com.netease.edu.kada.cache.core.storage;

import com.netease.edu.kada.cache.core.dto.CacheProjectDto;

/**
 * 当前依赖的项目缓存调用
 *
 * @author zhangchanglu
 * @since 2018/12/22 19:44.
 */
public interface ProjectCacheInvoke {
    /**
     * 所有缓存配置
     *
     * @param cacheProjectDto 缓存配置集合
     */
    void allCacheConfig(CacheProjectDto cacheProjectDto);

    void afterCacheEvict(String cacheName, String key, String appName);

    void afterCacheGet(String cacheName, String key, String appName);

    void afterClear(String cacheName, String appName);

    void afterCachePut(String cacheName, String key, String className, String methodName, String cacheConfigKey, String appName);
}

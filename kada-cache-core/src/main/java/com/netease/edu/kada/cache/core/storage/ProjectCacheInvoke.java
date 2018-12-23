package com.netease.edu.kada.cache.core.storage;

import com.netease.edu.kada.cache.core.dto.ClassCacheDto;

import java.util.Collection;

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
     * @param cacheConfig 缓存配置集合
     */
    default void allCacheConfig(Collection<ClassCacheDto> cacheConfig, String appName) {
    }

    default void afterCacheEvict(String cacheName, Object key, String appName) {
    }

    default void afterCacheGet(String cacheName, Object key, String appName) {
    }

    default void afterClear(String cacheName, String appName) {
    }

    default void afterCachePut(String cacheName, Object key, String className, String methodName, String cacheConfigKey, String appName) {
    }
}

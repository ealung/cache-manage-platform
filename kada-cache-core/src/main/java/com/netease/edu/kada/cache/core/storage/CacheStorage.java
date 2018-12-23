package com.netease.edu.kada.cache.core.storage;

import com.netease.edu.kada.cache.core.core.duplicate.CacheAspectSupport;
import com.netease.edu.kada.cache.core.dto.CacheKeyEntityDto;
import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import org.springframework.cache.Cache;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/09/30 15:58.
 */
public interface CacheStorage {

    Collection<ClassCacheDto> search(SearchParam searchParam, String appName);

    /**
     * 获取所有缓存
     */
    Collection<ClassCacheDto> getAllCache(String appName);

    /**
     * 删除缓存
     *
     * @param cacheName cacheConfig配置的cacheName
     * @param key       缓存key
     * @return 是否删除成功
     */
    void removeCacheName(String cacheName, String key, String appName);

    /**
     * 获取cacheConfig配置的cacheName下的所有key
     *
     * @param cacheName cacheConfig配置的cacheName
     * @return keys
     */
    Collection<CacheKeyEntityDto> findCacheKeys(String cacheName, String appName);

    /**
     * 删除对应类的key的缓存
     *
     * @param className 类名称
     * @param key       缓存的key
     * @return
     */
    void removeClassName(String className, String key, String appName);

}

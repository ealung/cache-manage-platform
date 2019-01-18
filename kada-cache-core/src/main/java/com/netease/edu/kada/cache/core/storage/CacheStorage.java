package com.netease.edu.kada.cache.core.storage;

import com.netease.edu.kada.cache.core.dto.PageInfo;
import com.netease.edu.kada.cache.core.vo.CacheKeyVo;
import com.netease.edu.kada.cache.core.vo.ClassCacheVo;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/09/30 15:58.
 */
public interface CacheStorage {

    PageInfo<ClassCacheVo> search(SearchParam searchParam, String appName, int pageIndex, int pageSize);

    /**
     * 获取所有缓存
     */
    PageInfo<ClassCacheVo> getAllCache(String appName, int pageIndex, int pageSize);

    /**
     * 获取所有的项目
     * @return 项目集合
     */
    Collection<String> getAllProject();

    /**
     * 删除缓存
     *
     * @param cacheName cacheConfig配置的cacheName
     * @param key       缓存key
     * @return 是否删除成功
     */
    boolean removeCacheName(String cacheName, String key, String appName);

    /**
     * 获取cacheConfig配置的cacheName下的所有key
     *
     * @param cacheName cacheConfig配置的cacheName
     * @return keys
     */
    Collection<CacheKeyVo> findCacheKeys(String cacheName, String appName);

    /**
     * 删除对应类的key的缓存
     *
     * @param className 类名称
     * @param key       缓存的key
     * @return
     */
    boolean removeClassName(String className, String key, String appName);

}

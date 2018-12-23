package com.netease.edu.kada.cache.service;

import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/12/22 18:28.
 */
@FeignClient("cache.manage.remote")
public interface CacheManageServerService extends ProjectCacheInvoke {
    /**
     * 初始化项目缓存配置
     *
     * @param cacheConfig 缓存配置
     */
    @RequestMapping(value = "/kada/cache/manage/init", method = RequestMethod.POST)
    void allCacheConfig(@RequestBody Collection<ClassCacheDto> cacheConfig, @RequestParam("appName") String appName);

    @RequestMapping(value = "/kada/cache/manage/clear", method = RequestMethod.POST)
    void afterClear(@RequestParam("cacheName") String cacheName, @RequestParam("appName") String appName);

    @RequestMapping(value = "/kada/cache/manage/get", method = RequestMethod.GET)
    void afterCacheGet(@RequestParam("cacheName") String cacheName, @RequestBody Object key, @RequestParam("appName") String appName);

    @RequestMapping(value = "/kada/cache/manage/evict", method = RequestMethod.POST)
    void afterCacheEvict(@RequestParam("cacheName") String cacheName, @RequestBody Object key, @RequestParam("appName") String appName);

    @RequestMapping(value = "/kada/cache/manage/put", method = RequestMethod.POST)
    void afterCachePut(@RequestParam("cacheName") String cacheName, @RequestBody Object key, @RequestParam("className") String className, @RequestParam("methodName") String methodName, @RequestParam("cacheConfigKey") String cacheConfigKey, @RequestParam("appName") String appName);
}

package com.netease.edu.kada.cache.controller;

import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/12/22 22:17.
 */
@RestController
@RequestMapping("/kada/cache/manage")
public class CacheManageCenterController {
    @Resource
    private CacheStorage cacheStorage;

    @GetMapping("/all")
    @ResponseBody
    public Collection<ClassCacheDto> allCache(String appName) {
        return cacheStorage.getAllCache(appName);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ClassCacheDto> search(SearchParam searchParam) {
        return cacheStorage.search(searchParam, searchParam.getAppName());
    }

    @PostMapping("/remove")
    @ResponseBody
    public void cacheRemove(String cacheName, String key, String appName) {
        cacheStorage.removeCacheName(cacheName, key, appName);
    }

    @PostMapping("/remove/cacheName")
    @ResponseBody
    public void cacheRemoveConfig(SearchParam searchParam) {
        if (searchParam.getModel() == 1) {
            cacheStorage.removeClassName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        } else {
            cacheStorage.removeCacheName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        }
    }
}

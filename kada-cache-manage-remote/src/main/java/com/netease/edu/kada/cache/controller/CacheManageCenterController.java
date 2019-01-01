package com.netease.edu.kada.cache.controller;

import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import com.netease.edu.kada.cache.core.vo.ClassCacheVo;
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
    public Collection<String> allProject() {
        return cacheStorage.getAllProject();
    }

    @GetMapping("/project/all")
    @ResponseBody
    public Collection<ClassCacheVo> projectAllCache(String appName) {
        return cacheStorage.getAllCache(appName);
    }

    @GetMapping("/search")
    @ResponseBody
    public Collection<ClassCacheVo> search(SearchParam searchParam) {
        return cacheStorage.search(searchParam, searchParam.getAppName());
    }

    @PostMapping("/remove")
    @ResponseBody
    public Boolean cacheRemove(String cacheName, String key, String appName) {
        return cacheStorage.removeCacheName(cacheName, key, appName);
    }

    @PostMapping("/remove/cacheName")
    @ResponseBody
    public Boolean cacheRemoveConfig(SearchParam searchParam) {
        if (searchParam.getModel() == 1) {
            return cacheStorage.removeClassName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        } else {
            return cacheStorage.removeCacheName(searchParam.getClassName(), searchParam.getCacheKey(), searchParam.getAppName());
        }
    }
}

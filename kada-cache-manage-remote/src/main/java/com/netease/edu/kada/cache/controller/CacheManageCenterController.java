package com.netease.edu.kada.cache.controller;

import com.netease.edu.kada.cache.core.dto.PageInfo;
import com.netease.edu.kada.cache.core.storage.CacheStorage;
import com.netease.edu.kada.cache.core.storage.SearchParam;
import com.netease.edu.kada.cache.core.vo.ClassCacheVo;
import org.springframework.data.domain.Page;
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
    public PageInfo<ClassCacheVo> projectAllCache(String appName, @RequestParam(defaultValue = "1") int pageIndex, @RequestParam(defaultValue = "20")  int pageSize) {
        return cacheStorage.getAllCache(appName, pageIndex, pageSize);
    }

    @GetMapping("/search")
    @ResponseBody
    public PageInfo<ClassCacheVo> search(SearchParam searchParam, int pageIndex, int pageSize) {
        return cacheStorage.search(searchParam, searchParam.getAppName(), pageIndex, pageSize);
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

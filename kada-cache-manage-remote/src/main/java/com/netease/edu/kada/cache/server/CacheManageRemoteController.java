package com.netease.edu.kada.cache.server;

import com.netease.edu.kada.cache.core.dto.ClassCacheDto;
import com.netease.edu.kada.cache.core.storage.ProjectCacheInvoke;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2018/12/22 19:06.
 */
@RequestMapping("/kada/cache/manage")
public class CacheManageRemoteController {
    @Resource
    private ProjectCacheInvoke projectCacheInvoke;

    /**
     * 所有缓存配置
     *
     * @param cacheConfig 缓存配置集合
     */
    @PostMapping("/init/cache")
    public void allCacheConfig(Collection<ClassCacheDto> cacheConfig, String appName) {
        projectCacheInvoke.allCacheConfig(cacheConfig, appName);
    }

    @RequestMapping("/clear")
    void clear(String cacheName, String appName) {
        projectCacheInvoke.afterClear(cacheName, appName);
    }

    @RequestMapping("/get")
    void afterCacheGet(String cacheName, Object key, String appName) {
        projectCacheInvoke.afterCacheGet(cacheName, key, appName);
    }

    @RequestMapping("/evict")
    void afterCacheEvict(String cacheName, Object key, String appName) {
        projectCacheInvoke.afterCacheEvict(cacheName, key, appName);
    }

    @RequestMapping("put")
    void afterCachePut(String cacheName, Object key, String className, String methodName, String cacheConfigKey, String appName) {
        projectCacheInvoke.afterCachePut(cacheName, key, className, methodName, cacheConfigKey, appName);
    }
}

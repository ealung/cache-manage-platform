package org.channel.cache.server;

import org.channel.cache.core.dto.CacheProjectDto;
import org.channel.cache.core.storage.ProjectCacheInvoke;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhangchanglu
 * @since 2018/12/22 19:06.
 */
@RestController
@RequestMapping("/kada/cache/manage")
public class CacheManageRemoteController {
    @Resource(name = "cacheManageRemoteService")
    private ProjectCacheInvoke projectCacheInvoke;

    /**
     * 所有缓存配置
     *
     * @param cacheProjectDto 缓存配置集合
     */
    @PostMapping("/init")
    public void allCacheConfig(@RequestBody CacheProjectDto cacheProjectDto) {
        projectCacheInvoke.allCacheConfig(cacheProjectDto);
    }

    @RequestMapping("/clear")
    void clear(String cacheName, String appName) {
        projectCacheInvoke.afterClear(cacheName, appName);
    }

    @RequestMapping("/get")
    void afterCacheGet(String cacheName, String key, String appName) {
        projectCacheInvoke.afterCacheGet(cacheName, key, appName);
    }

    @RequestMapping("/evict")
    void afterCacheEvict(String cacheName, String key, String appName) {
        projectCacheInvoke.afterCacheEvict(cacheName, key, appName);
    }

    @RequestMapping("put")
    void afterCachePut(String cacheName, String key, String className, String methodName, String cacheConfigKey, String appName) {
        projectCacheInvoke.afterCachePut(cacheName, key, className, methodName, cacheConfigKey, appName);
    }
}

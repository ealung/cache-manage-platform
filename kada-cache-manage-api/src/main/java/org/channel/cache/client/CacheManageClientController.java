package org.channel.cache.client;

import com.google.common.base.Strings;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhangchanglu
 * @since 2018/12/22 23:32.
 */
@RestController
@RequestMapping("/kada/cache/manage")
public class CacheManageClientController {
    @Resource
    private CacheManager cacheManager;

    @RequestMapping("/client/evict")
    @ResponseBody
    public void cacheEvict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (!Objects.isNull(cache)) {
            if (!Strings.isNullOrEmpty(key)) {
                cache.evict(key);
            } else {
                cache.clear();
            }
        }
    }
}

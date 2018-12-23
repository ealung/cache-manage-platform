package com.netease.edu.kada.cache.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhangchanglu
 * @since 2018/12/22 23:41.
 */
@FeignClient("cache.manage.client")
public interface CacheManageClientService {

    @RequestMapping(value = "/kada/cache/manage/evict", method = RequestMethod.GET)
    void cacheEvict(@RequestParam("cacheName") String cacheName, @RequestParam("key") String key);
}

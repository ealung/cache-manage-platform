package com.netease.edu.kada.cache;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangchanglu
 * @since 2018/12/22 18:00.
 */
@Configuration
public class KadaCacheManageRemoteConfig {
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager() {
        return new SimpleCacheManager();
    }

    @Bean
    public IRule cacheRule() {
        return new RandomRule() {

        };
    }
}

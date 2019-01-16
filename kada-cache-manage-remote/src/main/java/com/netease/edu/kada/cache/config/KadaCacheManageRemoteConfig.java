package com.netease.edu.kada.cache.config;

import com.netflix.loadbalancer.IRule;
import feign.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CacheLoadBalancerFeignClient;
import org.springframework.cloud.openfeign.ribbon.CachingManagerSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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
        return new CacheManageRandomRule();
    }

    @Bean
    @Primary
    @ConditionalOnMissingClass("org.springframework.retry.support.RetryTemplate")
    public CachingManagerSpringLoadBalancerFactory cachingLBClientFactory(
            SpringClientFactory factory) {
        return new CachingManagerSpringLoadBalancerFactory(factory);
    }

    @Bean
    public Client feignClient(CachingManagerSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory) {
        return new CacheLoadBalancerFeignClient(new Client.Default(null, null),
                cachingFactory, clientFactory);
    }
}

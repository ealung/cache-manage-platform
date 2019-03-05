package org.springframework.cloud.openfeign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangchanglu
 * @since 2019/01/17 15:52.
 */
public class CacheFeignAutoConfiguration extends FeignAutoConfiguration {
   /* @Bean
    public Targeter feignTargeter() {
        return new HystrixTargeter();
    }
    public CacheHystrixTargeter()*/
}

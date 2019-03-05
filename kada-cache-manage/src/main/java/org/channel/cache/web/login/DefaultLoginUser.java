package org.channel.cache.web.login;

import com.netease.edu.kada.cache.db.CacheSessionRepository;
import com.netease.edu.kada.cache.web.LoginUserConfig;
import lombok.extern.slf4j.Slf4j;
import org.channel.cache.db.CacheSessionRepository;
import org.channel.cache.web.LoginUserConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zhangchanglu
 * @since 2018/10/25 17:13.
 */
@Slf4j
@Configuration
public class DefaultLoginUser {
    @Resource
    private CacheSessionRepository cacheSessionRepository;

    @Bean
    @ConditionalOnMissingBean(LoginUserConfig.class)
    public LoginUserConfig cacheUserConfig() {
        return new AbstractLoginConfig();
    }

    public static void main(String[] args) {
        System.out.println();
    }
}

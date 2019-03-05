package org.channel.cache.web;

import com.netease.edu.kada.cache.web.login.LoginFilter;
import com.netease.edu.kada.cache.web.login.LoginServletParam;
import lombok.extern.slf4j.Slf4j;
import org.channel.cache.web.login.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @author zhangchanglu
 * @since 2018/05/14 20:06.
 */
@Configuration
@Slf4j
public class CacheWebConfig extends WebMvcConfigurerAdapter {
    @Resource
    private LoginUserConfig loginUserConfig;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        log.info("cache manage web enable :", loginUserConfig.isEnable());
        LoginServletParam loginServletParam = LoginServletParam.defaultBuilder()
                .resourcePath("cache.http.resources")
                .prefix("/org/channel/cache/core")
                .username(loginUserConfig.loginUser().getUserName())
                .password(loginUserConfig.loginUser().getUserPwd())
                .sessionUserKey("CACHESESSION")
                .loginUserConfig(loginUserConfig)
                .build();
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new LoginFilter(loginServletParam));
        registration.addUrlPatterns(loginServletParam.getPrefix() + "/*");
        registration.setName("loginFilter");
        registration.setOrder(1);
        return registration;
    }

}

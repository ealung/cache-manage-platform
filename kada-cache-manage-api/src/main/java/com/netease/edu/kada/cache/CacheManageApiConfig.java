package com.netease.edu.kada.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangchanglu
 * @since 2018/12/23 18:29.
 */
@Configuration
@EnableEurekaClient
@EnableFeignClients
@Slf4j
public class CacheManageApiConfig{

}

package com.netease.edu.kada.cache;

import com.netease.edu.kada.cache.core.core.duplicate.EnableNetEaseCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableNetEaseCaching
@EnableFeignClients
@EnableEurekaClient
public class CacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheApplication.class, args);
	}
}

package com.netease.edu.kada.cache;

import com.netease.edu.kada.cache.core.core.duplicate.EnableNetEaseCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNetEaseCaching
public class CacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheApplication.class, args);
	}
}

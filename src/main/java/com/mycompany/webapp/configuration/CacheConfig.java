package com.mycompany.webapp.configuration;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
	
	//특정 값을 cache 로 관리하게 해주는 관리빈
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("eamount"), 
                new ConcurrentMapCache("epeople"),
                new ConcurrentMapCache("estarttime")));
        return cacheManager;
    }
}

package org.got.erp.security.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.got.erp.security.cache.AclCache;
import org.got.erp.security.cache.impl.AclCacheImpl;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class ACLConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10_000)
                .recordStats());
        return cacheManager;
    }

    @Bean
    public AclCache aclCache(CacheManager cacheManager) {
        return new AclCacheImpl(cacheManager);
    }
}
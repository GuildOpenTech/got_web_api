package org.got.erp.security.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.got.erp.security.cache.AclCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class AclCacheImpl implements AclCache {
    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "aclCache";

    public AclCacheImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean get(String key, Function<String, Boolean> loader) {
        try {
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                return Boolean.TRUE.equals(cache.get(key, () -> loader.apply(key)));
            }
            return loader.apply(key);
        } catch (Exception e) {
            log.error("Error loading ACL cache for key: {}", key, e);
            return false;
        }
    }

    @Override
    public void invalidateForUser(Long userId) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(userId);
        }
    }

    @Override
    public void invalidateAll() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }
}
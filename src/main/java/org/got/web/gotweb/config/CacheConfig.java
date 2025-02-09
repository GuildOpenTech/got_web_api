package org.got.web.gotweb.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Configuration du cache avec Caffeine
 */
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter
public class CacheConfig {

    // Constantes pour les noms des caches
    public static final String CACHE_REVOKED_TOKENS = "revokedTokens";
    public static final String CACHE_USER_PERMISSIONS = "userPermissions";
    public static final String CACHE_USER_ROLES = "userRoles";
    public static final String CACHE_ROLES = "roles";
    public static final String CACHE_PERMISSIONS = "permissions";
    public static final String CACHE_DEPARTMENTS = "departments";
    public static final String CACHE_CONTEXTS = "contexts";

    private static final List<String> CACHE_NAMES = Arrays.asList(
            CACHE_REVOKED_TOKENS
//            CACHE_USER_PERMISSIONS,
//            CACHE_USER_ROLES,
//            CACHE_ROLES,
//            CACHE_PERMISSIONS,
//            CACHE_DEPARTMENTS,
//            CACHE_CONTEXTS
    );

    @Getter
    @Setter
    public static class CaffeineProperties {
        private int initialCapacity = 100;
        private int maximumSize = 10000;
        private long expireAfterWrite = 21600000; // 6 heures
        private long expireAfterAccess = 43200000; // 12 heures
        private boolean recordStats = false; // Désactivé par défaut pour des raisons de performances
    }

    private CaffeineProperties caffeine = new CaffeineProperties();

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(CACHE_NAMES);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(caffeine.getInitialCapacity())
                .maximumSize(caffeine.getMaximumSize())
                .expireAfterWrite(caffeine.getExpireAfterWrite(), TimeUnit.MILLISECONDS)
                .expireAfterAccess(caffeine.getExpireAfterAccess(), TimeUnit.MILLISECONDS)
                .recordStats();
    }
}

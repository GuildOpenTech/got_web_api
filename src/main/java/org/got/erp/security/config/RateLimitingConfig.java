package org.got.erp.security.config;

import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(100)
                        .refillGreedy(100, Duration.ofMinutes(1)))
                .addLimit(limit -> limit
                        .capacity(50)
                        .refillGreedy(50, Duration.ofSeconds(1)))
                .build();
    }
}

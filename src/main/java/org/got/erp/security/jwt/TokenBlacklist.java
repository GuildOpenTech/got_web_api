package org.got.erp.security.jwt;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class TokenBlacklist {
    private final Cache<String, Boolean> blacklist;
    private final BlockingQueue<String> revocationQueue;

    public TokenBlacklist() {
        this.blacklist = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofDays(1))
                .maximumSize(10_000)
                .build();

        this.revocationQueue = new LinkedBlockingQueue<>();
        Thread.startVirtualThread(this::processRevocations);
    }

    public void blacklist(String token) {
        blacklist.put(token, Boolean.TRUE);
        revocationQueue.offer(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.getIfPresent(token) != null;
    }

    private void processRevocations() {
        while (true) {
            try {
                String token = revocationQueue.take();
                // Traitement additionnel si nécessaire (ex: notification)
                log.debug("Token processed for revocation: {}", token);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

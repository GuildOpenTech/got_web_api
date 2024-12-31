package org.got.erp.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class JwtKeyRotationService {
    private final CopyOnWriteArrayList<JwtKey> keys;
    private final String baseSecret;
    private final long keyValiditySeconds;
    private final long keyOverlapSeconds;

    public JwtKeyRotationService(
            @Value("${security.jwt.secret}") String baseSecret,
            @Value("${security.jwt.key-validity-seconds:86400}") long keyValiditySeconds,  // 24 hours
            @Value("${security.jwt.key-overlap-seconds:3600}") long keyOverlapSeconds      // 1 hour
    ) {
        this.baseSecret = baseSecret;
        this.keyValiditySeconds = keyValiditySeconds;
        this.keyOverlapSeconds = keyOverlapSeconds;
        this.keys = new CopyOnWriteArrayList<>();
        rotateKey(); // Create initial key
    }

    @Scheduled(fixedRateString = "${security.jwt.key-rotation-check-ms:300000}") // 5 minutes
    public void checkAndRotateKeys() {
        cleanupExpiredKeys();
        if (shouldCreateNewKey()) {
            rotateKey();
        }
    }

    private void rotateKey() {
        Instant now = Instant.now();
        String keyId = UUID.randomUUID().toString();
        JwtKey newKey = new JwtKey(
            keyId,
            baseSecret + keyId, // Combine base secret with key ID for unique key
            now,
            now.plusSeconds(keyValiditySeconds)
        );
        keys.add(newKey);
        log.info("Created new JWT key with ID: {}", keyId);
    }

    private boolean shouldCreateNewKey() {
        return getCurrentKey() == null || 
               getCurrentKey().getExpiresAt().minusSeconds(keyOverlapSeconds).isBefore(Instant.now());
    }

    private void cleanupExpiredKeys() {
        Instant now = Instant.now();
        keys.removeIf(key -> key.getExpiresAt().isBefore(now.minusSeconds(keyOverlapSeconds)));
    }

    public JwtKey getCurrentKey() {
        return keys.stream()
                .filter(JwtKey::isValid)
                .findFirst()
                .orElse(null);
    }

    public List<JwtKey> getValidKeys() {
        return new ArrayList<>(keys);
    }
}

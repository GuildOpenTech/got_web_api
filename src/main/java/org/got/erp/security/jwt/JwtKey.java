package org.got.erp.security.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;

@Getter
public class JwtKey {
    private final String keyId;
    private final SecretKey key;
    private final Instant createdAt;
    private final Instant expiresAt;

    public JwtKey(String keyId, String secretKeyString, Instant createdAt, Instant expiresAt) {
        this.keyId = keyId;
        byte[] keyBytes = Base64.getEncoder().encode(secretKeyString.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        Instant now = Instant.now();
        return now.isAfter(createdAt) && now.isBefore(expiresAt);
    }
}

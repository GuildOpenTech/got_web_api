package org.got.erp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.got.erp.security.logging.SecurityLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Component
@Slf4j
public class JwtTokenProvider {
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private final TokenBlacklist tokenBlacklist;
    private final JwtKeyRotationService keyRotationService;
    private final SecurityLogger securityLogger;

    public JwtTokenProvider(
            @Value("${security.jwt.access-token-validity}") long accessTokenValidity,
            @Value("${security.jwt.refresh-token-validity}") long refreshTokenValidity,
            TokenBlacklist tokenBlacklist,
            JwtKeyRotationService keyRotationService,
            SecurityLogger securityLogger
    ) {
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.tokenBlacklist = tokenBlacklist;
        this.keyRotationService = keyRotationService;
        this.securityLogger = securityLogger;
    }

    public String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities, String ipAddress) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("type", "ACCESS");

        String token = createToken(claims, accessTokenValidity);
        securityLogger.logTokenCreation(token, ipAddress, "ACCESS");
        return token;
    }

    public String createRefreshToken(String username, String ipAddress) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("type", "REFRESH");
        String token = createToken(claims, refreshTokenValidity);
        securityLogger.logTokenCreation(token, ipAddress, "REFRESH");
        return token;
    }

    private String createToken(Claims claims, long validity) {
        JwtKey currentKey = keyRotationService.getCurrentKey();
        if (currentKey == null) {
            throw new JwtException("No valid signing key available");
        }

        var now = Instant.now();
        claims.setIssuedAt(Date.from(now));
        claims.setExpiration(Date.from(now.plusSeconds(validity)));

        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam("kid", currentKey.getKeyId())
                .signWith(currentKey.getKey())
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty() || tokenBlacklist.isBlacklisted(token)) {
            securityLogger.logInvalidToken(token, "Invalid or blacklisted token");
            return false;
        }

        try {
            String keyId = extractKeyId(token);
            if (keyId == null) {
                log.error("No key ID found in token");
                securityLogger.logInvalidToken(token, "No key ID found in token");
                return false;
            }

            SecretKey key = findKeyById(keyId);
            if (key == null) {
                log.error("No valid key found for ID: {}", keyId);
                securityLogger.logInvalidToken(token, "No valid key found for ID: " + keyId);
                return false;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!claims.getExpiration().before(new Date())) {
                securityLogger.logValidToken(token);
                return true;
            } else {
                securityLogger.logInvalidToken(token, "Token is expired");
                return false;
            }
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "Invalid JWT signature: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "Invalid JWT token: " + e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "JWT token is expired: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "JWT token is unsupported: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "JWT claims string is empty: " + e.getMessage());
            return false;
        }
    }

    private String extractKeyId(String token) {
        try {
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(token)
                    .getHeader()
                    .getKeyId();
        } catch (Exception e) {
            log.error("Error extracting key ID from token: {}", e.getMessage());
            securityLogger.logInvalidToken(token, "Error extracting key ID from token: " + e.getMessage());
            return null;
        }
    }

    private SecretKey findKeyById(String keyId) {
        return keyRotationService.getValidKeys().stream()
                .filter(key -> key.getKeyId().equals(keyId))
                .findFirst()
                .map(JwtKey::getKey)
                .orElse(null);
    }

    public String getUsername(String token) {
        String keyId = extractKeyId(token);
        if (keyId == null) {
            throw new JwtException("No key ID found in token");
        }

        SecretKey key = findKeyById(keyId);
        if (key == null) {
            throw new JwtException("No valid key found for token");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new JwtException("Error extracting username from token", e);
        }
    }
}
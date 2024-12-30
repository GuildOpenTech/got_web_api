package org.got.erp.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Component
@Slf4j
public class JwtTokenProvider {
    private final String secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private final TokenBlacklist tokenBlacklist;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.access-token-validity}") long accessTokenValidity,
            @Value("${security.jwt.refresh-token-validity}") long refreshTokenValidity,
            TokenBlacklist tokenBlacklist
    ) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.tokenBlacklist = tokenBlacklist;
    }

    public String createAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return createToken(claims, accessTokenValidity);
    }

    public String createRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        return createToken(claims, refreshTokenValidity);
    }

    private String createToken(Claims claims, long validity) {
        var now = Instant.now();
        var expiration = now.plusSeconds(validity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        if (tokenBlacklist.isBlacklisted(token)) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
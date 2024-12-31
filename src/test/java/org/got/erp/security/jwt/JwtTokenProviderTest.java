package org.got.erp.security.jwt;

import org.got.erp.security.logging.SecurityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private TokenBlacklist tokenBlacklist;
    
    @Mock
    private JwtKeyRotationService keyRotationService;
    
    @Mock 
    private SecurityLogger securityLogger;

    private JwtTokenProvider tokenProvider;
    
    private final String testIpAddress = "127.0.0.1";
    private final long accessTokenValidity = 3600L;
    private final long refreshTokenValidity = 86400L;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(
            accessTokenValidity,
            refreshTokenValidity,
            tokenBlacklist,
            keyRotationService,
            securityLogger
        );
        
        // Simuler une clé JWT valide
        JwtKey testKey = new JwtKey(
            "test-key-id",
            "test-secret-key-string",
            java.time.Instant.now().minusSeconds(60),
            java.time.Instant.now().plusSeconds(3600)
        );
        when(keyRotationService.getCurrentKey()).thenReturn(testKey);
    }

    @Test
    void shouldCreateValidAccessToken() {
        // Given
        String username = "testuser";
        Set<SimpleGrantedAuthority> authorities = Set.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        when(tokenBlacklist.isBlacklisted(anyString())).thenReturn(false);

        // When
        String token = tokenProvider.createAccessToken(username, authorities, testIpAddress);

        // Then
        assertThat(token).isNotNull();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsername(token)).isEqualTo(username);
    }

    @Test
    void shouldCreateValidRefreshToken() {
        // Given
        String username = "testuser";
        when(tokenBlacklist.isBlacklisted(anyString())).thenReturn(false);

        // When
        String token = tokenProvider.createRefreshToken(username, testIpAddress);

        // Then
        assertThat(token).isNotNull();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsername(token)).isEqualTo(username);
    }

    @Test
    void shouldRejectBlacklistedToken() {
        // Given
        String username = "testuser";
        Set<SimpleGrantedAuthority> authorities = Set.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        String token = tokenProvider.createAccessToken(username, authorities, testIpAddress);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(true);

        // When/Then
        assertThat(tokenProvider.validateToken(token)).isFalse();
    }
}

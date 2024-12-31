package org.got.erp.usersmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.exception.InvalidTokenException;
import org.got.erp.security.jwt.JwtTokenProvider;
import org.got.erp.security.jwt.TokenBlacklist;
import org.got.erp.security.logging.SecurityLogger;
import org.got.erp.usersmanagement.dto.AuthDTO;
import org.got.erp.usersmanagement.dto.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for handling authentication-related operations.
 * This includes user login, token refresh, and logout functionality.
 * All operations are secured and logged for audit purposes.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final SecurityLogger securityLogger;

    /**
     * Authenticates a user and generates new access and refresh tokens.
     *
     * @param request The login request containing username and password
     * @param ipAddress The IP address of the client making the request
     * @return TokenResponse containing access token, refresh token and validity period
     * @throws AuthenticationException if authentication fails
     */
    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request, String ipAddress) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = tokenProvider.createAccessToken(
                    userDetails.getUsername(),
                    userDetails.getAuthorities(),
                    ipAddress
            );
            String refreshToken = tokenProvider.createRefreshToken(userDetails.getUsername(), ipAddress);

            return new AuthDTO.TokenResponse(
                    accessToken,
                    refreshToken,
                    Instant.now().plusSeconds(tokenProvider.getAccessTokenValidity()),
                    "Bearer"
            );
        } catch (Exception e) {
            securityLogger.logAuthenticationFailure(request.username(), ipAddress, e.getMessage());
            throw e;
        }
    }

    /**
     * Invalidates a user's token and logs them out of the system.
     * The token is added to a blacklist to prevent its reuse.
     *
     * @param token The JWT token to invalidate
     * @param ipAddress The IP address of the client making the request
     */
    public void logout(String token, String ipAddress) {
        if (token != null && !token.isEmpty()) {
            String username = tokenProvider.getUsername(token);
            tokenBlacklist.blacklist(token);
            securityLogger.logTokenRevocation(username, ipAddress);
        }
    }

    /**
     * Generates a new access token using a valid refresh token.
     * The operation is logged and the refresh token is validated before generating new tokens.
     *
     * @param refreshToken The refresh token to use for generating new access token
     * @param ipAddress The IP address of the client making the request
     * @return TokenResponse containing new access token and validity period
     * @throws InvalidTokenException if the refresh token is invalid or expired
     */
    public AuthDTO.TokenResponse refreshToken(String refreshToken, String ipAddress) {
        try {
            if (!tokenProvider.validateToken(refreshToken)) {
                String username = tokenProvider.getUsername(refreshToken);
                securityLogger.logSuspiciousActivity(username, ipAddress, "invalid_refresh_token");
                throw new InvalidTokenException("Invalid refresh token");
            }

            String username = tokenProvider.getUsername(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            String newAccessToken = tokenProvider.createAccessToken(
                    username,
                    userDetails.getAuthorities(),
                    ipAddress
            );

            return new AuthDTO.TokenResponse(
                    newAccessToken,
                    refreshToken,
                    Instant.now().plusSeconds(tokenProvider.getAccessTokenValidity()),
                    "Bearer"
            );
        } catch (Exception e) {
            String username = tokenProvider.getUsername(refreshToken);
            securityLogger.logSuspiciousActivity(username, ipAddress, "refresh_token_failure");
            throw e;
        }
    }
}
package org.got.erp.usersmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.exception.InvalidTokenException;
import org.got.erp.security.jwt.JwtTokenProvider;
import org.got.erp.security.jwt.TokenBlacklist;
import org.got.erp.usersmanagement.dto.AuthDTO;
import org.got.erp.usersmanagement.dto.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklist tokenBlacklist;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = tokenProvider.createAccessToken(
                userDetails.getUsername(),
                userDetails.getAuthorities()
        );
        String refreshToken = tokenProvider.createRefreshToken(userDetails.getUsername());

        return new AuthDTO.TokenResponse(
                accessToken,
                refreshToken,
                Instant.now().plusSeconds(tokenProvider.getAccessTokenValidity()),
                "Bearer"
        );
    }

    public AuthDTO.TokenResponse refresh(AuthDTO.RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String username = tokenProvider.getUsername(request.refreshToken());
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenProvider.createAccessToken(
                username,
                userDetails.getAuthorities()
        );
        String newRefreshToken = tokenProvider.createRefreshToken(username);

        return new AuthDTO.TokenResponse(
                newAccessToken,
                newRefreshToken,
                Instant.now().plusSeconds(tokenProvider.getAccessTokenValidity()),
                "Bearer"
        );
    }

    public void logout(String token) {
        tokenBlacklist.blacklist(token);
    }
}
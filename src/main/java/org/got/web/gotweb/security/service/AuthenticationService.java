package org.got.web.gotweb.security.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.security.dto.LoginRequest;
import org.got.web.gotweb.security.dto.LoginResponse;
import org.got.web.gotweb.security.jwt.JwtTokenProvider;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.repository.GotUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour gérer l'authentification des utilisateurs
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final GotUserRepository userRepository;

    @Transactional
    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        GotUser user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = tokenProvider.generateToken(user);

        return LoginResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}

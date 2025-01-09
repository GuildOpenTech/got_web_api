package org.got.web.gotweb.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.security.dto.LoginRequest;
import org.got.web.gotweb.security.dto.LoginResponse;
import org.got.web.gotweb.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour gérer les endpoints d'authentification
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}

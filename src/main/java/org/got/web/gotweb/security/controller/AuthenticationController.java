package org.got.web.gotweb.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.TechnicalException;
import org.got.web.gotweb.security.dto.LoginRequest;
import org.got.web.gotweb.security.dto.LoginResponse;
import org.got.web.gotweb.security.dto.LogoutRequest;
import org.got.web.gotweb.security.dto.RefreshTokenRequest;
import org.got.web.gotweb.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour gérer l'authentification des utilisateurs
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Authentifie un utilisateur", description = "Authentifie un utilisateur avec son username et mot de passe")
    @ApiResponse(responseCode = "200", description = "Authentification réussie")
    @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchit un token", description = "Génère un nouveau token JWT à partir d'un refresh token")
    @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès")
    @ApiResponse(responseCode = "401", description = "Refresh token invalide")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnecte un utilisateur", description = "Révoque tous les tokens de l'utilisateur")
    @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    public ResponseEntity<Void> logout(@Valid LogoutRequest logoutRequest) {
        //TODO: Implement logout
        throw new TechnicalException("Not implemented yet") {
        };
    }
}

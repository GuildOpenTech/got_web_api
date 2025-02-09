package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.dto.user.request.ResetPasswordDto;
import org.got.web.gotweb.user.service.GotUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credentials")
@RequiredArgsConstructor
@Tag(name = "Credentials", description = "Credentials API")
public class CredentialsController {

    private final GotUserService userService;


    @Operation(
            summary = "Demander une réinitialisation de mot de passe",
            description = "Envoie un email pour réinitialiser le mot de passe d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Demande de réinitialisation envoyée avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")})
    @PostMapping("/password-reset-request")
    public ResponseEntity<Void> requestPasswordReset(@Parameter(description = "Adresse email de l'utilisateur")
                                                     @RequestParam @NotBlank String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.accepted().build();
    }


    @Operation(
            summary = "Réinitialiser le mot de passe avec un token",
            description = "Réinitialise le mot de passe d'un utilisateur à l'aide d'un token de réinitialisation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")})
    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password-reset/{token}")
    @Operation(
            summary = "Vérifier le token de réinitialisation de mot de passe",
            description = "Vérifie le token de réinitialisation de mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de réinitialisation vérifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")})
    public ResponseEntity<Void> verifyPasswordResetToken(@PathVariable String token) {
        userService.verifyPasswordResetToken(token);
        return ResponseEntity.ok().build();
    }
}

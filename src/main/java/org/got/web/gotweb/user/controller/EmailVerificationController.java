package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.service.GotUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verify-email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "API de vérification de l'email")
public class EmailVerificationController {

    private final GotUserService userService;
    private final GotUserMapper userMapper;

    @GetMapping("/{token}")
    @Operation(
            summary = "Vérifier l'email de l'utilisateur",
            description = "Vérifie l'email de l'utilisateur à l'aide d'un token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email vérifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> verifyEmail(@PathVariable String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}

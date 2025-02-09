package org.got.web.gotweb.security.jwt;

import java.time.Instant;
import java.util.List;

/**
 * Records pour la gestion des tokens JWT
 */
public sealed interface JwtTokens {
    
    /**
     * Représente une paire de tokens (access + refresh)
     * @param accessToken Token d'accès
     * @param refreshToken Token de rafraîchissement
     */
    record TokenPair(String accessToken, String refreshToken) implements JwtTokens {}

    /**
     * Claims personnalisés pour un utilisateur
     * @param userId ID de l'utilisateur
     * @param username Nom d'utilisateur
     * @param roles Liste des rôles (format: ROLE_NAME:DEPARTMENT_ID:CONTEXT_ID)
     * @param permissions Liste des permissions
     */
    record UserClaims(
            Long userId,
            String username,
            List<String> roles,
            List<String> permissions
    ) implements JwtTokens {}

    /**
     * Informations sur un token JWT
     * @param subject Sujet du token (username)
     * @param issuedAt Date d'émission
     * @param expiresAt Date d'expiration
     * @param claims Claims personnalisés
     */
    record TokenInfo(
            String subject,
            Instant issuedAt,
            Instant expiresAt,
            UserClaims claims
    ) implements JwtTokens {}

    /**
     * Token révoqué
     * @param jti ID unique du token
     * @param expiresAt Date d'expiration
     */
    record RevokedToken(
            String jti,
            Instant expiresAt
    ) implements JwtTokens {}
}

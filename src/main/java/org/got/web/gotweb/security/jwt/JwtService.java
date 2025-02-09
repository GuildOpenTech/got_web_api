package org.got.web.gotweb.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.exception.TokenException;
import org.got.web.gotweb.security.config.JwtConfig;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.got.web.gotweb.config.CacheConfig.CACHE_REVOKED_TOKENS;

/**
 * Service de gestion des tokens JWT
 * Gère la génération, validation et révocation des tokens
 */
@Slf4j
@Service
public class JwtService {

    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String USER_ID_CLAIM = "userId";
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";


    private final JwtConfig jwtConfig;
    private final KeyPair keyPair;
    private final Cache revokedTokensCache;

    public JwtService(JwtConfig jwtConfig, KeyPair keyPair, CacheManager cacheManager) {
        this.jwtConfig = jwtConfig;
        this.keyPair = keyPair;
        this.revokedTokensCache = cacheManager.getCache(CACHE_REVOKED_TOKENS );
        if (this.revokedTokensCache == null) {
            throw new TokenException("Cache 'revokedTokens' non configuré");
        }
    }

    /**
     * Génère une paire de tokens pour un utilisateur
     * 
     * @param principal Principal de l'utilisateur
     * @param claims Claims personnalisés
     * @return Paire de tokens (access + refresh)
     */
    public JwtTokens.TokenPair generateTokenPair(Object principal, JwtTokens.UserClaims claims) {
        String subject = principal instanceof UserDetails ? 
                ((UserDetails) principal).getUsername() : principal.toString();
        // Génère l'access token
        String accessToken = generateToken(subject, claims, TOKEN_TYPE_ACCESS, jwtConfig.getExpiration());
        // Génère le refresh token
        String refreshToken = generateToken(subject, claims, TOKEN_TYPE_REFRESH, jwtConfig.getRefreshToken().getExpiration());

        return new JwtTokens.TokenPair(accessToken, refreshToken);
    }

    /**
     * Génère un token JWT
     * 
     * @param subject Sujet du token
     * @param claims Claims personnalisés
     * @param tokenType Type de token (ACCESS ou REFRESH)
     * @param expiration Durée de validité en millisecondes
     * @return Token JWT
     */
    private String generateToken(String subject, JwtTokens.UserClaims claims, String tokenType, long expiration) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expiration);

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TOKEN_TYPE_CLAIM, tokenType);
        claimsMap.put(USER_ID_CLAIM, claims.userId());
        claimsMap.put(ROLES_CLAIM, claims.roles());
        claimsMap.put(PERMISSIONS_CLAIM, claims.permissions());

        return Jwts.builder()
                .claims(claimsMap)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .id(UUID.randomUUID().toString())
                .issuer(jwtConfig.getIssuer())
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS512)
                .compact();
    }

    /**
     * Valide un token JWT
     * 
     * @param token Token à valider
     * @return Informations sur le token si valide
     * @throws JwtException Si le token est invalide
     */
    public JwtTokens.TokenInfo validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // Vérifie si le token est révoqué
            String jti = claims.getId();
            if (isTokenRevoked(jti)) {
                throw new TokenException("Token révoqué");
            }

            // Vérifie le type de token
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            if (tokenType == null) {
                throw new TokenException("Type de token manquant");
            }

            // Vérifie l'émetteur
            String issuer = claims.getIssuer();
            if (!jwtConfig.getIssuer().equals(issuer)) {
                throw new TokenException("Émetteur du token invalide");
            }

            // Vérifie la présence des claims obligatoires
            Long userId = claims.get(USER_ID_CLAIM, Long.class);
            if (userId == null) {
                throw new TokenException("ID utilisateur manquant");
            }

            // Extrait et valide les claims personnalisés
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get(ROLES_CLAIM, List.class);
            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get(PERMISSIONS_CLAIM, List.class);

            if (roles == null || permissions == null) {
                throw new TokenException("Rôles ou permissions manquants");
            }

            JwtTokens.UserClaims userClaims = new JwtTokens.UserClaims(userId, claims.getSubject(), roles, permissions);

            return new JwtTokens.TokenInfo(
                claims.getSubject(),
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant(),
                userClaims
            );

        } catch (SignatureException e) {
            throw new TokenException("Signature JWT invalide", e);
        } catch (ExpiredJwtException e) {
            throw new TokenException.TokenExpiredException(token, TokenException.ACCESS, e);
        } catch (Exception e) {
            throw new TokenException("Erreur lors de la validation du token", e);
        }
    }

    /**
     * Parse un token JWT
     * 
     * @param token Token à parser
     * @return Claims du token
     * @throws JwtException Si le token est invalide
     */
    private Claims parseToken(String token) {
        try {
            var jwtParser = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build();

            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new TokenException("Erreur lors du parsing du token", e);
        }
    }

    /**
     * Révoque un token
     * 
     * @param token Token à révoquer
     * @throws JwtException Si le token est invalide ou déjà révoqué
     */
    public void revokeToken(String token) {
        try {
            Claims claims = parseToken(token);
            String jti = claims.getId();
            Date expiryDate = claims.getExpiration();

            // Vérifie si le token est déjà révoqué
            if (isTokenRevoked(jti)) {
                throw new TokenException("Token déjà révoqué");
            }

            // Vérifie si le token n'est pas déjà expiré
            if (expiryDate.before(new Date())) {
                throw new TokenException("Impossible de révoquer un token déjà expiré");
            }

            // Ajoute le token à la liste des tokens révoqués avec sa date d'expiration
            revokedTokensCache.put(jti, new JwtTokens.RevokedToken(jti, expiryDate.toInstant()));

            log.info("Token {} révoqué avec succès", jti);
        } catch (JwtException e) {
            throw new TokenException("Erreur lors de la révocation du token", e);
        }
    }

    /**
     * Révoque tous les tokens d'un utilisateur
     * Cette méthode est utile lors d'un changement de mot de passe ou d'une déconnexion forcée
     * 
     * @param username Nom d'utilisateur
     */
    public void revokeTokens(String username) {
        try {
            // On ajoute une entrée spéciale dans le cache pour marquer tous les tokens de l'utilisateur comme révoqués
            String userRevokeKey = "user_revoke:" + username;
            revokedTokensCache.put(userRevokeKey, new JwtTokens.RevokedToken(
                userRevokeKey,
                Instant.now().plusMillis(jwtConfig.getRefreshToken().getExpiration())
            ));
        } catch (Exception e) {
            throw new TokenException("Erreur lors de la révocation des tokens", e);
        }
    }

    /**
     * Vérifie si un token est révoqué
     * 
     * @param token ID unique du token
     * @return true si le token est révoqué
     */
    public boolean isTokenRevoked(String token) {
        try {
            var revokedToken = revokedTokensCache.get(token, JwtTokens.RevokedToken.class);
            return revokedToken != null && revokedToken.expiresAt().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }
}

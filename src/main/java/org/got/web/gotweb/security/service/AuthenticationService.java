package org.got.web.gotweb.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.exception.AuthenticationException;
import org.got.web.gotweb.security.dto.LoginRequest;
import org.got.web.gotweb.security.dto.LoginResponse;
import org.got.web.gotweb.security.dto.RefreshTokenRequest;
import org.got.web.gotweb.security.jwt.JwtService;
import org.got.web.gotweb.security.jwt.JwtTokens;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.UserRole;
import org.got.web.gotweb.user.repository.GotUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service pour gérer l'authentification des utilisateurs
 * Gère le login, la génération des tokens JWT et le refresh des tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GotUserRepository userRepository;

    /**
     * Authentifie un utilisateur et génère les tokens JWT
     *
     * @param loginRequest Requête de login avec username et password
     * @return Réponse contenant les tokens et les informations utilisateur
     * @throws AuthenticationException si les identifiants sont invalides ou l'utilisateur n'existe pas
     */
    @Transactional
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // Authentifie l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Configure le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Récupère l'utilisateur
            GotUser user = (GotUser) authentication.getPrincipal();

            // Crée les claims utilisateur
            JwtTokens.UserClaims claims = createUserClaims(user);

            // Génère les tokens
            JwtTokens.TokenPair tokens = jwtService.generateTokenPair(authentication.getPrincipal(), claims);

            // Construit et retourne la réponse
            return buildLoginResponse(user, tokens);

        } catch (BadCredentialsException e) {
            log.warn("Tentative de connexion échouée pour l'utilisateur : {}", loginRequest.getUsername());
            throw new AuthenticationException("Identifiants invalides", e);
        }
    }

    /**
     * Rafraîchit un token JWT expiré
     *
     * @param refreshRequest Requête contenant le refresh token
     * @return Nouveaux tokens JWT
     * @throws AuthenticationException si le refresh token est invalide ou l'utilisateur n'existe pas
     */
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest refreshRequest) {
        try {
            // Valide le refresh token
            JwtTokens.TokenInfo tokenInfo = jwtService.validateToken(refreshRequest.getRefreshToken());
            
            // Récupère l'utilisateur
            GotUser user = userRepository.findByUsername(tokenInfo.subject())
                    .orElseThrow(() -> new AuthenticationException("Utilisateur non trouvé : " + tokenInfo.subject()));

            // Crée les claims utilisateur
            JwtTokens.UserClaims claims = createUserClaims(user);

            // Génère de nouveaux tokens
            JwtTokens.TokenPair tokens = jwtService.generateTokenPair(user, claims);

            // Construit et retourne la réponse
            return buildLoginResponse(user, tokens);

        } catch (Exception e) {
            throw new AuthenticationException("Erreur lors du rafraîchissement du token", e);
        }
    }

    /**
     * Révoque tous les tokens d'un utilisateur
     * Utile lors de la déconnexion ou du changement de mot de passe
     *
     * @param username Nom d'utilisateur
     */
    @Transactional
    public void revokeAllUserTokens(String username) {
        jwtService.revokeTokens(username);
        log.info("Tous les tokens ont été révoqués pour l'utilisateur : {}", username);
    }

    /**
     * Crée les claims utilisateur pour les tokens JWT.
     * Inclut toutes les permissions de l'utilisateur provenant de :
     * - Permissions directes des UserRoles
     * - Permissions héritées des Roles
     * - Permissions par défaut des Departments
     */
    private JwtTokens.UserClaims createUserClaims(GotUser user) {
        // Extrait les rôles avec leur contexte
        List<String> roles = user.getUserRoles().stream()
                .map(this::formatRole)
                .toList();

        // Extrait toutes les permissions uniques de l'utilisateur
        List<String> permissions = user.getUserRoles().stream()
                .flatMap(role -> role.getAllPermissions().stream())
                .map(p -> "PERM_" + p.getId())
                .distinct()
                .toList();

        return new JwtTokens.UserClaims(
                user.getId(),
                user.getUsername(),
                roles,
                permissions
        );
    }

    /**
     * Formate un rôle utilisateur avec son contexte
     * Format: ROLE_ID:DEPARTMENT_ID:CONTEXT_ID
     */
    private String formatRole(UserRole userRole) {
        return String.format("ROLE_%s:%d:%d",
                userRole.getRole().getId(),
                userRole.getDepartment().getId(),
                userRole.getContext().getId());
    }

    /**
     * Construit la réponse de login à partir de l'utilisateur et des tokens
     */
    private LoginResponse buildLoginResponse(GotUser user, JwtTokens.TokenPair tokens) {
        return LoginResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}

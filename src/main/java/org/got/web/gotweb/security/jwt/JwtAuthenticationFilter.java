package org.got.web.gotweb.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.exception.ApiError;
import org.got.web.gotweb.security.evaluator.RuleContext;
import org.got.web.gotweb.security.evaluator.service.AccessControlService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ERROR_HEADER = "X-JWT-Error";

    private final JwtService jwtService;
    private final AccessControlService accessControlService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                // Pas de token : on retourne 401 Unauthorized
                setErrorResponse(response, "Token manquant", HttpStatus.UNAUTHORIZED);
                return;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());
            JwtTokens.TokenInfo tokenInfo;
            try {
                tokenInfo = jwtService.validateToken(token);
            } catch (Exception e) {
                log.error("Erreur lors de la validation du token : {}", e.getMessage(), e);
                setErrorResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
                return;
            }

            Authentication authentication = new JwtAuthenticationToken(tokenInfo);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Construction du RuleContext à partir des claims extraites du token
            RuleContext ruleContext = buildRuleContext(tokenInfo.claims());
            String requestUrl = request.getRequestURI();

            if (!accessControlService.hasAccess(requestUrl, ruleContext)) {
                setErrorResponse(response,
                        "Accès refusé : vous n'avez pas les droits nécessaires pour accéder à cette ressource",
                        HttpStatus.FORBIDDEN);
                return;
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Erreur inattendue dans le filtre JWT : {}", e.getMessage(), e);
            setErrorResponse(response, "Erreur d'authentification", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth")
                || path.startsWith("/public")
                || path.startsWith("/error")
                || path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger")
        || path.startsWith("/api/v1/admin/endpoints/assign-condition-groups");

    }

    /**
     * Construit un RuleContext à partir des claims extraites du token.
     * Exemples :
     * - Pour un rôle au format "ROLE_1:1:1", on extrait :
     *   • roleId = 1, departmentId = 1, contextId = 1
     * - Pour une permission au format "PERM_6", on extrait 6
     * Le RuleContext est construit via son builder.
     */
    public RuleContext buildRuleContext(JwtTokens.UserClaims userClaims) {
        Set<Long> roleIds = new HashSet<>();
        Set<Long> departmentIds = new HashSet<>();
        Set<Long> contextIds = new HashSet<>();

        for (String role : userClaims.roles()) {
            // Suppression du préfixe "ROLE_" et découpage par ":"
            String[] parts = role.replace("ROLE_", "").split(":");
            if (parts.length == 3) {
                roleIds.add(Long.parseLong(parts[0]));       // ROLE_ID
                departmentIds.add(Long.parseLong(parts[1]));   // DEPARTMENT_ID
                contextIds.add(Long.parseLong(parts[2]));      // CONTEXT_ID
            }
        }

        Set<Long> permissionIds = userClaims.permissions().stream()
                .map(perm -> Long.parseLong(perm.replace("PERM_", "")))
                .collect(Collectors.toSet());

        // Construction du RuleContext à l'aide du builder (assurez-vous que RuleContext possède un builder)
        return RuleContext.builder()
                .roles(roleIds)
                .permissions(permissionIds)
                .departments(departmentIds)
                .contexts(contextIds)
                .user(userClaims.userId())
                .build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            String requestUri = request.getRequestURI();
            log.error("Authentication error on request [{}]: {}", requestUri, authException.getMessage());
            setErrorResponse(response, authException.getMessage(), HttpStatus.UNAUTHORIZED);
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String requestUri = request.getRequestURI();
            log.error("Access denied on request [{}]: {}", requestUri, accessDeniedException.getMessage());
            setErrorResponse(response, accessDeniedException.getMessage(), HttpStatus.FORBIDDEN);
        };
    }


    private void setErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        // Construction de l'objet ApiError
        ApiError error = ApiError.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        // Conversion de l'objet en JSON (avec Jackson)
        String jsonError = objectMapper.writeValueAsString(error);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(jsonError);
    }


}

package org.got.web.gotweb.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ERROR_HEADER = "X-JWT-Error";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            log.debug(" [JWT Filter] Authentification existante: {}", 
                     existingAuth != null ? String.format("Auth présente (authenticated=%s, principal=%s)", 
                                                       existingAuth.isAuthenticated(), 
                                                       existingAuth.getPrincipal()) 
                                        : "Aucune authentification");

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                var tokenInfo = jwtService.validateToken(token);
                var authentication = new JwtAuthenticationToken(tokenInfo);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                log.error(" [JWT Filter] Erreur lors de la validation du token", e);
                // Removed SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(" [JWT Filter] Erreur inattendue dans JwtAuthenticationFilter", e);
            // Removed SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void setErrorResponse(HttpServletResponse response, String message, HttpStatus status) {
        log.debug("Configuration de la réponse d'erreur: {} - {}", status, message);
        response.setHeader(ERROR_HEADER, message);
        response.setStatus(status.value());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }
}
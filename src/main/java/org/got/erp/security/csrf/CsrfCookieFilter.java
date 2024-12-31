package org.got.erp.security.csrf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            // Ajoute le token dans les headers pour le rendre accessible au frontend
            response.setHeader(csrf.getHeaderName(), csrf.getToken());
            
            // Ajoute aussi le nom de l'en-tête attendu
            response.setHeader("X-CSRF-HEADER", csrf.getHeaderName());
            response.setHeader("X-CSRF-PARAM", csrf.getParameterName());
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Optionnel : ne pas filtrer les requêtes OPTIONS pour éviter les problèmes CORS
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}

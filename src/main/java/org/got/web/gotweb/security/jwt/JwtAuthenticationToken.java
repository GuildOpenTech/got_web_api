package org.got.web.gotweb.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Token d'authentification JWT personnalisé
 * Permet de stocker les informations de l'utilisateur et ses permissions
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    
    private final String principal;
    @Getter
    private final JwtTokens.TokenInfo tokenInfo;
    private final String credentials;

    public JwtAuthenticationToken(JwtTokens.TokenInfo tokenInfo) {
        super(buildAuthorities(tokenInfo));
        this.principal = tokenInfo.subject();
        this.tokenInfo = tokenInfo;
        this.credentials = "";
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    private static Collection<GrantedAuthority> buildAuthorities(JwtTokens.TokenInfo tokenInfo) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Ajoute les rôles avec le préfixe ROLE_
        tokenInfo.claims().roles().stream()
            .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
            .forEach(authorities::add);
        
        // Ajoute les permissions directement (sans préfixe)
        tokenInfo.claims().permissions().stream()
            .map(perm -> new SimpleGrantedAuthority(perm.startsWith("PERM_") ? perm : "PERM_" + perm))
            .forEach(authorities::add);
        
        return authorities;
    }
}

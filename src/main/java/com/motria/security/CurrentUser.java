package com.motria.security;

import com.motria.user.Roles;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Datos del usuario autenticado en la petición actual, leídos del JWT.
 * Reemplaza al antiguo TenantContext: ya no hay ThreadLocal que limpiar a mano.
 */
@Component
public class CurrentUser {

    public Long userId() {
        return TokenClaims.userId(token().getToken());
    }

    public Long workshopId() {
        return TokenClaims.workshopId(token().getToken());
    }

    public boolean isAdmin() {
        return token().getAuthorities().stream()
                .anyMatch(authority -> Roles.ADMIN.equals(authority.getAuthority()));
    }

    private JwtAuthenticationToken token() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return jwtToken;
        }
        throw new AuthenticationCredentialsNotFoundException("No hay un usuario autenticado");
    }
}

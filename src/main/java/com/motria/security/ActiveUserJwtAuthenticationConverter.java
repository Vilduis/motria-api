package com.motria.security;

import com.motria.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

/**
 * Convierte un JWT válido en la autenticación de la petición.
 * Además comprueba que la cuenta siga activa: si un administrador desactiva a un técnico,
 * su token deja de servir de inmediato en lugar de esperar a que expire.
 */
@Component
@RequiredArgsConstructor
public class ActiveUserJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;
    private final JwtGrantedAuthoritiesConverter authoritiesConverter = rolesFromAuthoritiesClaim();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (!userRepository.existsByIdAndActiveTrue(TokenClaims.userId(jwt))) {
            throw new DisabledException("La cuenta está deshabilitada");
        }
        return new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt), jwt.getSubject());
    }

    /** Lee el claim "authorities" tal cual (ADMIN, TECHNICAL), sin el prefijo SCOPE_ por defecto. */
    private static JwtGrantedAuthoritiesConverter rolesFromAuthoritiesClaim() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName(TokenClaims.AUTHORITIES);
        converter.setAuthorityPrefix("");
        return converter;
    }
}

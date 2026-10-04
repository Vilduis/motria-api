package com.motria.security;

import org.springframework.security.oauth2.jwt.Jwt;

/** Nombres de los claims propios del JWT de Motria y cómo leerlos. */
public final class TokenClaims {

    public static final String AUTHORITIES = "authorities";
    public static final String USER_ID = "userId";
    public static final String WORKSHOP_ID = "workshopId";

    private TokenClaims() {
    }

    public static Long userId(Jwt jwt) {
        return longClaim(jwt, USER_ID);
    }

    public static Long workshopId(Jwt jwt) {
        return longClaim(jwt, WORKSHOP_ID);
    }

    private static Long longClaim(Jwt jwt, String name) {
        Number value = jwt.getClaim(name);
        if (value == null) {
            throw new IllegalStateException("El token no contiene el claim '" + name + "'");
        }
        return value.longValue();
    }
}

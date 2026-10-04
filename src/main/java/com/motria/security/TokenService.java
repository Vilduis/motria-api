package com.motria.security;

import com.motria.config.AppProperties;
import com.motria.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/** Emite los JWT que el frontend envía en el header {@code Authorization: Bearer ...}. */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final AppProperties properties;
    private final Clock clock;

    public String generateToken(User user) {
        Instant now = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(now.plus(properties.jwt().expiration()))
                .claim(TokenClaims.AUTHORITIES, user.authorityNames())
                .claim(TokenClaims.USER_ID, user.getId())
                .claim(TokenClaims.WORKSHOP_ID, user.getWorkshop().getId())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}

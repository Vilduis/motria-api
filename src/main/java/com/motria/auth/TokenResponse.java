package com.motria.auth;

/** Respuesta de login y registro. {@code authorities} va separado por ";" como espera el frontend. */
public record TokenResponse(
        String jwtToken,
        Long userId,
        String authorities,
        Long workshopId,
        String workshopName,
        boolean mustChangePassword
) {
}

package com.motria.auth;

/** Datos del usuario que tiene la sesión abierta (GET /auth/me). */
public record MeResponse(
        Long userId,
        String email,
        String displayName,
        String authorities,
        boolean active,
        boolean mustChangePassword,
        Long workshopId,
        String workshopName
) {
}

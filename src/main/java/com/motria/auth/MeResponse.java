package com.motria.auth;

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

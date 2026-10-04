package com.motria.user;

/** @param authorities roles separados por ";" (formato que ya usa el frontend). */
public record UserResponse(
        Long id,
        String email,
        boolean active,
        boolean mustChangePassword,
        String authorities
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isActive(),
                user.isMustChangePassword(),
                String.join(";", user.authorityNames()));
    }
}

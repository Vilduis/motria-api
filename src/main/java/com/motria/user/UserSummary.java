package com.motria.user;

/** Vista reducida de la cuenta, usada dentro de otras respuestas (por ejemplo, la de un técnico). */
public record UserSummary(
        Long id,
        String email,
        boolean active,
        boolean mustChangePassword
) {

    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getEmail(), user.isActive(), user.isMustChangePassword());
    }
}

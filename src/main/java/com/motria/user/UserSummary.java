package com.motria.user;

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

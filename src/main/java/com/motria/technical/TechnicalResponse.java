package com.motria.technical;

import com.motria.user.UserSummary;

public record TechnicalResponse(
        Long id,
        String name,
        String lastName,
        String specialty,
        UserSummary user
) {

    public static TechnicalResponse from(Technical technical) {
        return new TechnicalResponse(
                technical.getId(),
                technical.getName(),
                technical.getLastName(),
                technical.getSpecialty(),
                UserSummary.from(technical.getUser()));
    }
}

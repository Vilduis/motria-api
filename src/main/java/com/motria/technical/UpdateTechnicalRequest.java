package com.motria.technical;

import jakarta.validation.constraints.NotBlank;

public record UpdateTechnicalRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El apellido es obligatorio") String lastName,
        String specialty
) {
}

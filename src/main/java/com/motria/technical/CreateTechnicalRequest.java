package com.motria.technical;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateTechnicalRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El apellido es obligatorio") String lastName,
        String specialty,
        @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email
) {
}

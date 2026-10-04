package com.motria.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param password opcional: si viene vacío se mantiene la contraseña actual.
 * @param active   opcional: si viene null se mantiene el estado actual.
 */
public record UpdateUserRequest(
        @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        Boolean active
) {
}

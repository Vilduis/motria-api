package com.motria.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpos de las peticiones de /auth. Son pequeños, por eso se agrupan en un solo archivo. */
public final class AuthRequests {

    private AuthRequests() {
    }

    public record Login(
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
            @NotBlank(message = "La contraseña es obligatoria") String password
    ) {
    }

    public record ChangePassword(
            @NotBlank(message = "La contraseña actual es obligatoria") String currentPassword,
            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres") String newPassword
    ) {
    }

    public record ForgotPassword(
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email
    ) {
    }

    public record ResetPassword(
            @NotBlank(message = "El token es obligatorio") String token,
            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String newPassword
    ) {
    }
}

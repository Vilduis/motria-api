package com.motria.workshop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterWorkshopRequest(
        @NotBlank(message = "El nombre del taller es obligatorio") String workshopName,
        @NotBlank(message = "El nombre del dueño es obligatorio") String ownerName,
        @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        @Size(max = 50) String phone,
        String address
) {
}

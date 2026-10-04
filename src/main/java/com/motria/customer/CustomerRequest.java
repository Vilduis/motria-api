package com.motria.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        String lastName,
        @Size(max = 50) String phone,
        @Email(message = "Email inválido") String email
) {
}

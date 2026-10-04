package com.motria.vehicle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
        @NotBlank(message = "La placa es obligatoria") @Size(max = 20) String plate,
        String brand,
        String model,
        @Min(value = 1900, message = "Año inválido") @Max(value = 2100, message = "Año inválido") Integer year,
        @NotNull(message = "El cliente es obligatorio") Long customerId
) {
}

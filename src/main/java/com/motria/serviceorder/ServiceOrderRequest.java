package com.motria.serviceorder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * @param date   opcional: si no se envía, se usa la fecha y hora actual.
 * @param status solo se usa al editar; una orden nueva siempre empieza en PENDIENTE.
 */
public record ServiceOrderRequest(
        LocalDateTime date,
        @NotNull(message = "El vehículo es obligatorio") Long vehicleId,
        @NotNull(message = "El cliente es obligatorio") Long customerId,
        @NotNull(message = "El técnico es obligatorio") Long technicalId,
        @Size(max = 1000, message = "El diagnóstico no puede superar los 1000 caracteres") String diagnosis,
        OrderStatus status
) {
}

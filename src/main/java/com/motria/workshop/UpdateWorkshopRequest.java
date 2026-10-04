package com.motria.workshop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWorkshopRequest(
        @NotBlank(message = "El nombre del taller es obligatorio") String workshopName,
        @NotBlank(message = "El nombre del dueño es obligatorio") String ownerName,
        @Size(max = 50) String phone,
        String address
) {
}

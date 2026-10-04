package com.motria.vehicle;

import com.motria.customer.CustomerResponse;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String plate,
        String brand,
        String model,
        Integer year,
        CustomerResponse customer,
        LocalDateTime createdAt
) {

    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                CustomerResponse.from(vehicle.getCustomer()),
                vehicle.getCreatedAt());
    }
}

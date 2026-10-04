package com.motria.serviceorder;

import com.motria.customer.CustomerResponse;
import com.motria.technical.TechnicalResponse;
import com.motria.vehicle.VehicleResponse;

import java.time.LocalDateTime;

public record ServiceOrderResponse(
        Long id,
        LocalDateTime date,
        VehicleResponse vehicle,
        CustomerResponse customer,
        TechnicalResponse technical,
        String diagnosis,
        OrderStatus status
) {

    public static ServiceOrderResponse from(ServiceOrder order) {
        return new ServiceOrderResponse(
                order.getId(),
                order.getDate(),
                VehicleResponse.from(order.getVehicle()),
                CustomerResponse.from(order.getCustomer()),
                TechnicalResponse.from(order.getTechnical()),
                order.getDiagnosis(),
                order.getStatus());
    }
}

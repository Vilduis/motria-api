package com.motria.dashboard;

import com.motria.serviceorder.OrderStatus;
import com.motria.serviceorder.ServiceOrder;

import java.time.LocalDateTime;

public record RecentOrderResponse(
        Long orderId,
        String vehiclePlate,
        String vehicleBrand,
        String vehicleModel,
        String customerName,
        String technicalName,
        String diagnosis,
        OrderStatus status,
        LocalDateTime date
) {

    public static RecentOrderResponse from(ServiceOrder order) {
        return new RecentOrderResponse(
                order.getId(),
                order.getVehicle().getPlate(),
                order.getVehicle().getBrand(),
                order.getVehicle().getModel(),
                order.getCustomer().fullName(),
                order.getTechnical().fullName(),
                order.getDiagnosis(),
                order.getStatus(),
                order.getDate());
    }
}

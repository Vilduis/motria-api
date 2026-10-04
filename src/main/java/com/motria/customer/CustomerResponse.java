package com.motria.customer;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String name,
        String lastName,
        String phone,
        String email,
        LocalDateTime createdAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedAt());
    }
}

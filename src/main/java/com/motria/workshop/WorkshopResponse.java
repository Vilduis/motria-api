package com.motria.workshop;

public record WorkshopResponse(
        Long id,
        String workshopName,
        String ownerName,
        String email,
        String phone,
        String address,
        Plan plan,
        boolean active
) {

    public static WorkshopResponse from(Workshop workshop) {
        return new WorkshopResponse(
                workshop.getId(),
                workshop.getWorkshopName(),
                workshop.getOwnerName(),
                workshop.getEmail(),
                workshop.getPhone(),
                workshop.getAddress(),
                workshop.getPlan(),
                workshop.isActive());
    }
}

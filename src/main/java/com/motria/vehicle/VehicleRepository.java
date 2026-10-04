package com.motria.vehicle;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @EntityGraph(attributePaths = "customer")
    List<Vehicle> findAllByWorkshopIdOrderByCreatedAtDesc(Long workshopId);

    @EntityGraph(attributePaths = "customer")
    List<Vehicle> findAllByWorkshopIdAndCustomerId(Long workshopId, Long customerId);

    Optional<Vehicle> findByIdAndWorkshopId(Long id, Long workshopId);

    boolean existsByCustomerId(Long customerId);

    boolean existsByWorkshopIdAndPlate(Long workshopId, String plate);

    boolean existsByWorkshopIdAndPlateAndIdNot(Long workshopId, String plate, Long id);

    long countByWorkshopId(Long workshopId);

    long countByWorkshopIdAndCreatedAtBetween(Long workshopId, LocalDateTime from, LocalDateTime to);
}

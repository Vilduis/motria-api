package com.motria.serviceorder;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Todas las consultas reciben el {@code workshopId} para que un taller nunca vea datos de otro.
 * El {@link EntityGraph} carga las relaciones en una sola consulta (evita el problema N+1).
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    @EntityGraph(attributePaths = {"vehicle", "vehicle.customer", "customer", "technical", "technical.user"})
    Optional<ServiceOrder> findByIdAndWorkshopId(Long id, Long workshopId);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.customer", "customer", "technical", "technical.user"})
    List<ServiceOrder> findAllByWorkshopIdOrderByDateDesc(Long workshopId);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.customer", "customer", "technical", "technical.user"})
    List<ServiceOrder> findAllByWorkshopIdAndTechnicalIdOrderByDateDesc(Long workshopId, Long technicalId);

    @EntityGraph(attributePaths = {"vehicle", "vehicle.customer", "customer", "technical", "technical.user"})
    List<ServiceOrder> findAllByWorkshopIdAndStatusOrderByDateDesc(Long workshopId, OrderStatus status);

    @EntityGraph(attributePaths = {"vehicle", "customer", "technical"})
    List<ServiceOrder> findTop5ByWorkshopIdOrderByDateDesc(Long workshopId);

    @EntityGraph(attributePaths = {"vehicle", "customer", "technical"})
    List<ServiceOrder> findTop5ByWorkshopIdAndTechnicalIdOrderByDateDesc(Long workshopId, Long technicalId);

    boolean existsByCustomerId(Long customerId);

    boolean existsByVehicleId(Long vehicleId);

    Optional<ServiceOrder> findFirstByVehicleIdAndStatusNot(Long vehicleId, OrderStatus status);

    Optional<ServiceOrder> findFirstByVehicleIdAndStatusNotAndIdNot(Long vehicleId, OrderStatus status, Long id);

    boolean existsByTechnicalId(Long technicalId);

    long countByWorkshopIdAndStatus(Long workshopId, OrderStatus status);

    long countByWorkshopIdAndDateBetween(Long workshopId, LocalDateTime from, LocalDateTime to);

    long countByWorkshopIdAndStatusAndDateBetween(Long workshopId, OrderStatus status, LocalDateTime from, LocalDateTime to);

    long countByWorkshopIdAndTechnicalId(Long workshopId, Long technicalId);

    long countByWorkshopIdAndTechnicalIdAndStatus(Long workshopId, Long technicalId, OrderStatus status);
}

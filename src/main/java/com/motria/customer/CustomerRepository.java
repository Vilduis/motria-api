package com.motria.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findAllByWorkshopIdOrderByCreatedAtDesc(Long workshopId);

    Optional<Customer> findByIdAndWorkshopId(Long id, Long workshopId);

    long countByWorkshopId(Long workshopId);

    long countByWorkshopIdAndCreatedAtBetween(Long workshopId, LocalDateTime from, LocalDateTime to);
}

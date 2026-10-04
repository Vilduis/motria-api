package com.motria.technical;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicalRepository extends JpaRepository<Technical, Long> {

    @EntityGraph(attributePaths = "user")
    List<Technical> findAllByWorkshopIdOrderByName(Long workshopId);

    Optional<Technical> findByIdAndWorkshopId(Long id, Long workshopId);

    Optional<Technical> findByUserId(Long userId);
}

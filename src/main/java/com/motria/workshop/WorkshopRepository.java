package com.motria.workshop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {

    boolean existsByEmailIgnoreCase(String email);
}

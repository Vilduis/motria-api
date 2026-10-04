package com.motria.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIdAndActiveTrue(Long id);

    List<User> findAllByWorkshopIdOrderByEmail(Long workshopId);

    Optional<User> findByIdAndWorkshopId(Long id, Long workshopId);
}

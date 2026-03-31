package com.skillforge.backend.repository;

import com.skillforge.backend.entity.AppUser;
import com.skillforge.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmailAndRole(String email, Role role);
    List<AppUser> findAllByOrderByCreatedAtDesc();
    long countByRole(Role role);
    long countByCreatedAtAfter(java.time.LocalDateTime createdAt);
}

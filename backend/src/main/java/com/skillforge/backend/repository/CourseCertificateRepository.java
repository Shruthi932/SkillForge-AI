package com.skillforge.backend.repository;

import com.skillforge.backend.entity.CourseCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseCertificateRepository extends JpaRepository<CourseCertificate, Long> {
    Optional<CourseCertificate> findByUserIdAndTopic(Long userId, String topic);
    Optional<CourseCertificate> findByCertificateCode(String certificateCode);
}

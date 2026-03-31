package com.skillforge.backend.repository;

import com.skillforge.backend.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByUserIdOrderBySubmittedAtDesc(Long userId);
    long countByUserId(Long userId);
}

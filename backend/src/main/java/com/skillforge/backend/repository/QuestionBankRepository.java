package com.skillforge.backend.repository;

import com.skillforge.backend.entity.QuestionBankItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionBankRepository extends JpaRepository<QuestionBankItem, Long> {
    List<QuestionBankItem> findAllByOrderByCreatedAtDesc();
    long countByTopicIgnoreCase(String topic);
    long countByTopicIgnoreCaseAndIdNot(String topic, Long id);
    List<QuestionBankItem> findByTopicInAndDifficultyOrderByUsageCountDescCreatedAtDesc(List<String> topics, String difficulty);
}

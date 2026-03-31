package com.skillforge.backend.repository;

import com.skillforge.backend.entity.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {
    List<LessonCompletion> findByUserIdAndTopicOrderByCompletedAtAsc(Long userId, String topic);
    Optional<LessonCompletion> findByUserIdAndTopicAndLessonKey(Long userId, String topic, String lessonKey);
    long countByUserIdAndTopic(Long userId, String topic);
    void deleteByUserIdAndTopicAndLessonKey(Long userId, String topic, String lessonKey);
}

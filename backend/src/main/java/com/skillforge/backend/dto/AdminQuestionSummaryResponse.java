package com.skillforge.backend.dto;

import java.time.LocalDateTime;

public class AdminQuestionSummaryResponse {

    private final Long id;
    private final String topic;
    private final String difficulty;
    private final String questionText;
    private final Integer usageCount;
    private final LocalDateTime createdAt;

    public AdminQuestionSummaryResponse(Long id,
                                        String topic,
                                        String difficulty,
                                        String questionText,
                                        Integer usageCount,
                                        LocalDateTime createdAt) {
        this.id = id;
        this.topic = topic;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.usageCount = usageCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package com.skillforge.backend.dto;

import java.time.LocalDateTime;

public class ExamAttemptResponse {

    private final Long id;
    private final String topicSummary;
    private final String assessmentType;
    private final String difficulty;
    private final Integer questionCount;
    private final Integer correctAnswers;
    private final Integer scorePercentage;
    private final Integer answeredCount;
    private final Integer timeLimitMinutes;
    private final LocalDateTime submittedAt;

    public ExamAttemptResponse(Long id,
                               String topicSummary,
                               String assessmentType,
                               String difficulty,
                               Integer questionCount,
                               Integer correctAnswers,
                               Integer scorePercentage,
                               Integer answeredCount,
                               Integer timeLimitMinutes,
                               LocalDateTime submittedAt) {
        this.id = id;
        this.topicSummary = topicSummary;
        this.assessmentType = assessmentType;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.correctAnswers = correctAnswers;
        this.scorePercentage = scorePercentage;
        this.answeredCount = answeredCount;
        this.timeLimitMinutes = timeLimitMinutes;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTopicSummary() {
        return topicSummary;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public Integer getScorePercentage() {
        return scorePercentage;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}

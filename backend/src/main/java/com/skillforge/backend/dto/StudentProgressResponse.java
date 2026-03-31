package com.skillforge.backend.dto;

import java.util.List;

public class StudentProgressResponse {

    private final Integer examsCompleted;
    private final Integer averageScore;
    private final Integer bestScore;
    private final Integer latestScore;
    private final List<ExamAttemptResponse> recentAttempts;

    public StudentProgressResponse(Integer examsCompleted,
                                   Integer averageScore,
                                   Integer bestScore,
                                   Integer latestScore,
                                   List<ExamAttemptResponse> recentAttempts) {
        this.examsCompleted = examsCompleted;
        this.averageScore = averageScore;
        this.bestScore = bestScore;
        this.latestScore = latestScore;
        this.recentAttempts = recentAttempts;
    }

    public Integer getExamsCompleted() {
        return examsCompleted;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public Integer getLatestScore() {
        return latestScore;
    }

    public List<ExamAttemptResponse> getRecentAttempts() {
        return recentAttempts;
    }
}

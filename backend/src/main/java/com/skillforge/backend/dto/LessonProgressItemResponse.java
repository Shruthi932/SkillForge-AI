package com.skillforge.backend.dto;

import java.time.LocalDateTime;

public class LessonProgressItemResponse {

    private final String lessonKey;
    private final String lessonTitle;
    private final LocalDateTime completedAt;

    public LessonProgressItemResponse(String lessonKey, String lessonTitle, LocalDateTime completedAt) {
        this.lessonKey = lessonKey;
        this.lessonTitle = lessonTitle;
        this.completedAt = completedAt;
    }

    public String getLessonKey() {
        return lessonKey;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}

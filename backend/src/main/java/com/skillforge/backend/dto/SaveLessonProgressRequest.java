package com.skillforge.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SaveLessonProgressRequest {

    @NotBlank
    private String topic;

    @NotBlank
    private String lessonKey;

    @NotBlank
    private String lessonTitle;

    private boolean completed;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLessonKey() {
        return lessonKey;
    }

    public void setLessonKey(String lessonKey) {
        this.lessonKey = lessonKey;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

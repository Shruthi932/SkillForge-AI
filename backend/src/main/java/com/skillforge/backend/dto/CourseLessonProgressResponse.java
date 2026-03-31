package com.skillforge.backend.dto;

import java.util.List;

public class CourseLessonProgressResponse {

    private final String topic;
    private final Integer completedCount;
    private final Integer totalLessons;
    private final List<LessonProgressItemResponse> completedLessons;

    public CourseLessonProgressResponse(String topic,
                                        Integer completedCount,
                                        Integer totalLessons,
                                        List<LessonProgressItemResponse> completedLessons) {
        this.topic = topic;
        this.completedCount = completedCount;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public List<LessonProgressItemResponse> getCompletedLessons() {
        return completedLessons;
    }
}

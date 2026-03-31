package com.skillforge.backend.dto;

import java.util.List;

public class ExamQuestionResponse {

    private final Long id;
    private final String topic;
    private final String difficulty;
    private final String questionText;
    private final List<String> options;
    private final Integer correctOptionIndex;

    public ExamQuestionResponse(Long id,
                                String topic,
                                String difficulty,
                                String questionText,
                                List<String> options,
                                Integer correctOptionIndex) {
        this.id = id;
        this.topic = topic;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
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

    public List<String> getOptions() {
        return options;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}

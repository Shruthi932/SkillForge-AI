package com.skillforge.backend.dto;

import java.util.List;

public class ExamGenerationResponse {

    private final List<String> topics;
    private final String difficulty;
    private final Integer requestedQuestionCount;
    private final Integer generatedQuestionCount;
    private final List<ExamQuestionResponse> questions;

    public ExamGenerationResponse(List<String> topics,
                                  String difficulty,
                                  Integer requestedQuestionCount,
                                  Integer generatedQuestionCount,
                                  List<ExamQuestionResponse> questions) {
        this.topics = topics;
        this.difficulty = difficulty;
        this.requestedQuestionCount = requestedQuestionCount;
        this.generatedQuestionCount = generatedQuestionCount;
        this.questions = questions;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getRequestedQuestionCount() {
        return requestedQuestionCount;
    }

    public Integer getGeneratedQuestionCount() {
        return generatedQuestionCount;
    }

    public List<ExamQuestionResponse> getQuestions() {
        return questions;
    }
}

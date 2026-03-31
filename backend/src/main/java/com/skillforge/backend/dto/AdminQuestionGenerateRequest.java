package com.skillforge.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminQuestionGenerateRequest {

    @NotBlank
    @Size(max = 80)
    private String topic;

    @NotBlank
    @Size(max = 30)
    private String difficulty;

    @Min(1)
    @Max(10)
    private Integer count = 3;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

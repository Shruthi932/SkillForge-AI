package com.skillforge.backend.controller;

import com.skillforge.backend.dto.AdminQuestionCreateRequest;
import com.skillforge.backend.dto.AdminQuestionGenerateRequest;
import com.skillforge.backend.dto.AdminQuestionSummaryResponse;
import com.skillforge.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
public class TrainerController {

    private final AuthService authService;

    public TrainerController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/questions")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public AdminQuestionSummaryResponse createQuestion(@Valid @RequestBody AdminQuestionCreateRequest request) {
        return authService.createQuestion(request);
    }

    @PostMapping("/questions/generate")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public List<AdminQuestionSummaryResponse> generateQuestions(@Valid @RequestBody AdminQuestionGenerateRequest request) {
        return authService.generateQuestions(request);
    }
}

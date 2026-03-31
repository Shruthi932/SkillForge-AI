package com.skillforge.backend.controller;

import com.skillforge.backend.dto.AdminAnalyticsResponse;
import com.skillforge.backend.dto.AdminQuestionCreateRequest;
import com.skillforge.backend.dto.AdminQuestionGenerateRequest;
import com.skillforge.backend.dto.AdminQuestionSummaryResponse;
import com.skillforge.backend.dto.AdminUserSummaryResponse;
import com.skillforge.backend.dto.RegisterRequest;
import com.skillforge.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserSummaryResponse> users() {
        return authService.getAllUsers();
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserSummaryResponse createUser(@Valid @RequestBody RegisterRequest request) {
        return authService.createAdminUser(request);
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminAnalyticsResponse analytics() {
        return authService.getAdminAnalytics();
    }

    @GetMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminQuestionSummaryResponse> questions() {
        return authService.getQuestionBank();
    }

    @PostMapping("/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminQuestionSummaryResponse createQuestion(@Valid @RequestBody AdminQuestionCreateRequest request) {
        return authService.createQuestion(request);
    }

    @PutMapping("/questions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminQuestionSummaryResponse updateQuestion(@PathVariable Long id,
                                                       @Valid @RequestBody AdminQuestionCreateRequest request) {
        return authService.updateQuestion(id, request);
    }

    @DeleteMapping("/questions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestion(@PathVariable Long id) {
        authService.deleteQuestion(id);
    }

    @PostMapping("/questions/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminQuestionSummaryResponse> generateQuestions(@Valid @RequestBody AdminQuestionGenerateRequest request) {
        return authService.generateQuestions(request);
    }
}

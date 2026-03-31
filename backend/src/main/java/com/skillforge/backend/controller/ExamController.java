package com.skillforge.backend.controller;

import com.skillforge.backend.dto.ExamGenerationRequest;
import com.skillforge.backend.dto.ExamGenerationResponse;
import com.skillforge.backend.dto.ExamAttemptResponse;
import com.skillforge.backend.dto.SaveExamAttemptRequest;
import com.skillforge.backend.dto.StudentProgressResponse;
import com.skillforge.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final AuthService authService;

    public ExamController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/generate")
    public ExamGenerationResponse generate(@Valid @RequestBody ExamGenerationRequest request) {
        return authService.generateExam(request);
    }

    @PostMapping("/attempts")
    public ResponseEntity<ExamAttemptResponse> saveAttempt(Authentication authentication,
                                                          @Valid @RequestBody SaveExamAttemptRequest request) {
        return ResponseEntity.ok(authService.saveExamAttempt(authentication.getName(), request));
    }

    @GetMapping("/attempts/me")
    public ResponseEntity<StudentProgressResponse> myProgress(Authentication authentication) {
        return ResponseEntity.ok(authService.getStudentProgress(authentication.getName()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}

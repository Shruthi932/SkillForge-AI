package com.skillforge.backend.controller;

import com.skillforge.backend.dto.CourseCertificateRequest;
import com.skillforge.backend.dto.CourseCertificateResponse;
import com.skillforge.backend.dto.CourseLessonProgressResponse;
import com.skillforge.backend.dto.SaveLessonProgressRequest;
import com.skillforge.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    private final AuthService authService;

    public LearningController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/lessons/progress")
    public ResponseEntity<CourseLessonProgressResponse> lessonProgress(Authentication authentication,
                                                                      @RequestParam String topic,
                                                                      @RequestParam(required = false) Integer totalLessons) {
        return ResponseEntity.ok(authService.getCourseLessonProgress(authentication.getName(), topic, totalLessons));
    }

    @PostMapping("/lessons/progress")
    public ResponseEntity<CourseLessonProgressResponse> saveLessonProgress(Authentication authentication,
                                                                          @Valid @RequestBody SaveLessonProgressRequest request) {
        return ResponseEntity.ok(authService.saveLessonProgress(authentication.getName(), request));
    }

    @GetMapping("/certificate")
    public ResponseEntity<CourseCertificateResponse> getCertificate(Authentication authentication,
                                                                   @RequestParam String topic) {
        return ResponseEntity.ok(authService.getCourseCertificate(authentication.getName(), topic));
    }

    @GetMapping("/certificate/public")
    public ResponseEntity<CourseCertificateResponse> getPublicCertificate(@RequestParam String code) {
        return ResponseEntity.ok(authService.getCourseCertificateByCode(code));
    }

    @PostMapping("/certificate")
    public ResponseEntity<CourseCertificateResponse> generateCertificate(Authentication authentication,
                                                                        @Valid @RequestBody CourseCertificateRequest request) {
        return ResponseEntity.ok(authService.generateCourseCertificate(authentication.getName(), request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}

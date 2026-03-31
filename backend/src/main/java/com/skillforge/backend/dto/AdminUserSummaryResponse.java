package com.skillforge.backend.dto;

import java.time.LocalDateTime;

public class AdminUserSummaryResponse {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String role;
    private final LocalDateTime createdAt;

    public AdminUserSummaryResponse(Long id, String fullName, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

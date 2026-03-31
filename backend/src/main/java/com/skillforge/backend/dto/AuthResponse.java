package com.skillforge.backend.dto;

public class AuthResponse {

    private final String token;
    private final String name;
    private final String email;
    private final String role;
    private final String message;

    public AuthResponse(String token, String name, String email, String role, String message) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}


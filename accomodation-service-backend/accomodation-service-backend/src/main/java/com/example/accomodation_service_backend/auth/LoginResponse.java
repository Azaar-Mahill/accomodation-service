package com.example.accomodation_service_backend.auth;

import com.example.accomodation_service_backend.auth.UserRole;

public class LoginResponse {
    private String email;
    private UserRole role;

    public LoginResponse(String email, UserRole role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}


package com.example.accomodation_service_backend.auth;

public class LoginResponse {
    private String email;
    private UserRole role;
    private String token;     // 👈 new

    public LoginResponse(String email, UserRole role, String token) {
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public String getToken() { return token; }
}

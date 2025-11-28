package com.example.accomodation_service_backend.auth;

import com.example.accomodation_service_backend.auth.UserRole;

public class SignupRequest {
    private String email;
    private String password;
    private UserRole role;   // CUSTOMER or ADMIN

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}


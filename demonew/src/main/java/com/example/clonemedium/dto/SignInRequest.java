package com.example.clonemedium.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
    private String providerId;  // For social login
    private String providerToken;  // For social login
    private String authProvider;  // LOCAL, GOOGLE, FACEBOOK, APPLE, X
} 
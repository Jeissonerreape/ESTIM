package com.estim.javaapi.presentation.auth;

public record RegisterUserRequest(
    String email,
    String password,
    String displayName
) {}

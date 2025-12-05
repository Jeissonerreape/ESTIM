package com.estim.javaapi.presentation.auth;

public record LoginRequest(
    String email,
    String password
) {}

package com.estim.javaapi.application.auth;

/**
 * Input data for registering a new user.
 * Comes from controllers / API layer, but stays independent of HTTP/JSON.
 */
public record RegisterUserCommand(
    String email,
    String password,
    String displayName
) {}

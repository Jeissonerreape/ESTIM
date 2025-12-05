package com.estim.javaapi.application.auth;

/**
 * Input data for authenticating a user with email/password.
 */
public record AuthenticateUserCommand(
    String email,
    String password
) {}

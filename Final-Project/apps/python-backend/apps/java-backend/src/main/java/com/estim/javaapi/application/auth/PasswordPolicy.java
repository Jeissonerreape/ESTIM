package com.estim.javaapi.application.auth;

/**
 * Encapsulates password validation rules.
 * Implemented in the application/infrastructure layer (e.g. length, complexity).
 */
public interface PasswordPolicy {

    /**
     * Returns true if the password satisfies the policy.
     */
    boolean isValid(String rawPassword);

    /**
     * Optionally returns a human-readable description of the policy.
     */
    default String description() {
        return "Password must meet security policy requirements.";
    }
}

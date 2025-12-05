package com.estim.javaapi.application.auth;

import org.springframework.stereotype.Component;

/**
 * Password rules:
 * - At least 8 characters
 * - At least one digit
 * - At least one special character (non letter/digit)
 */
@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    @Override
    public boolean isValid(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        if (rawPassword.length() < 8) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : rawPassword.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetter(c) && !Character.isDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasDigit && hasSpecial;
    }

    @Override
    public String description() {
        return "Password must be at least 8 characters long and include at least one digit and one special character.";
    }
}

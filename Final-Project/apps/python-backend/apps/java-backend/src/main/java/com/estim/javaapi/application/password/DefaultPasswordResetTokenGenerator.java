package com.estim.javaapi.application.password;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Default implementation of PasswordResetTokenGenerator.
 * Generates a random, URL-safe token.
 */
@Component
public class DefaultPasswordResetTokenGenerator implements PasswordResetTokenGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(randomBytes);
    }
}

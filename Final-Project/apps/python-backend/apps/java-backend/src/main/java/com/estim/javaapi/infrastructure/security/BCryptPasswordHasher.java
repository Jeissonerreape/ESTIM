package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.application.auth.PasswordHasher;
import com.estim.javaapi.domain.user.PasswordHash;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * BCrypt-based implementation of PasswordHasher.
 *
 * Requires dependency:
 *   implementation "org.mindrot:jbcrypt:0.4"
 */
@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final int strength;

    /**
     * @param strength the log rounds to use, e.g., 10-14
     */
    public BCryptPasswordHasher(@Value("${bcrypt.strength:12}") int strength){
        this.strength = strength;
    }

    @Override
    public PasswordHash hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        String salt = BCrypt.gensalt(strength);
        String hashed = BCrypt.hashpw(rawPassword, salt);
        return new PasswordHash(hashed);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(hash, "hash must not be null");
        return BCrypt.checkpw(rawPassword, hash.value());
    }
}

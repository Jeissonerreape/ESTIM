package com.estim.javaapi.domain.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a password reset token associated with a user.
 * The token is a separate aggregate since its lifecycle is independent from the User aggregate.
 */
public class PasswordResetToken {

    private final PasswordResetTokenId id;
    private final UserId userId;
    private final String token;        // random string sent to user
    private final Instant expiresAt;
    private boolean used;

    public PasswordResetToken(PasswordResetTokenId id,
                              UserId userId,
                              String token,
                              Instant expiresAt,
                              boolean used) {

        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.token = Objects.requireNonNull(token);
        this.expiresAt = Objects.requireNonNull(expiresAt);
        this.used = used;
    }

    public PasswordResetTokenId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public String token() {
        return token;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public boolean used() {
        return used;
    }

    /**
     * Marks the token as used.
     */
    public void markUsed() {
        this.used = true;
    }

    /**
     * Checks whether the token has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Factory method for creating a new password reset token.
     */
    public static PasswordResetToken create(UserId userId,
                                            String token,
                                            Instant expiresAt) {
        return new PasswordResetToken(
            new PasswordResetTokenId(UUID.randomUUID()),
            userId,
            token,
            expiresAt,
            false
        );
    }
}

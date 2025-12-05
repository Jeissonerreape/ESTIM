package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised when a new user registers using email/password.
 */
public final class UserRegistered implements DomainEvent {

    private final UserId userId;
    private final String email;
    private final Instant occurredAt;

    public UserRegistered(UserId userId, String email, Instant occurredAt) {
        this.userId = userId;
        this.email = email;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public UserId userId() {
        return userId;
    }

    public String email() {
        return email;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

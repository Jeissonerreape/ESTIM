package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised when a user successfully verifies their email address.
 */
public final class UserEmailVerified implements DomainEvent {

    private final UserId userId;
    private final Instant occurredAt;

    public UserEmailVerified(UserId userId, Instant occurredAt) {
        this.userId = userId;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public UserId userId() {
        return userId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

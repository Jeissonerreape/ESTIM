package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised when a banned user is restored back to ACTIVE status.
 */
public final class UserUnbanned implements DomainEvent {

    private final UserId userId;
    private final Instant occurredAt;

    public UserUnbanned(UserId userId, Instant occurredAt) {
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

package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised whenever a user's profile information changes.
 */
public final class UserProfileUpdated implements DomainEvent {

    private final UserId userId;
    private final Instant occurredAt;

    public UserProfileUpdated(UserId userId, Instant occurredAt) {
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

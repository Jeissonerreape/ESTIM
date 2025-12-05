package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

public class UserLoggedIn implements DomainEvent {

    private final UserId userId;
    private final Instant occurredAt;

    public UserLoggedIn(UserId userId, Instant occurredAt) {
        this.userId = Objects.requireNonNull(userId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public UserId userId() {
        return userId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

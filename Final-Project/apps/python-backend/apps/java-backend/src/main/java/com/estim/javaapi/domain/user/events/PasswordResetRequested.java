package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.PasswordResetTokenId;

import java.time.Instant;
import java.util.Objects;

public class PasswordResetRequested implements DomainEvent {

    private final UserId userId;
    private final PasswordResetTokenId tokenId;
    private final Instant occurredAt;

    public PasswordResetRequested(UserId userId,
                                  PasswordResetTokenId tokenId,
                                  Instant occurredAt) {
        this.userId = Objects.requireNonNull(userId);
        this.tokenId = Objects.requireNonNull(tokenId);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public UserId userId() {
        return userId;
    }

    public PasswordResetTokenId tokenId() {
        return tokenId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}


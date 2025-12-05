package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.OAuthProvider;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised when an OAuth account (Google, Steam, etc.)
 * is linked to an existing user.
 */
public final class OAuthAccountLinked implements DomainEvent {

    private final UserId userId;
    private final OAuthProvider provider;
    private final String externalUserId;
    private final Instant occurredAt;

    public OAuthAccountLinked(UserId userId, OAuthProvider provider, String externalUserId, Instant occurredAt) {
        this.userId = userId;
        this.provider = provider;
        this.externalUserId = externalUserId;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public UserId userId() {
        return userId;
    }

    public OAuthProvider provider() {
        return provider;
    }

    public String externalUserId() {
        return externalUserId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

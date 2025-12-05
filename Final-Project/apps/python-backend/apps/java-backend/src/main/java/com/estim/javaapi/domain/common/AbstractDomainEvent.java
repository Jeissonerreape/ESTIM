package com.estim.javaapi.domain.common;

import java.time.Instant;

public abstract class AbstractDomainEvent implements DomainEvent {

    private final Instant occurredAt;

    protected AbstractDomainEvent() {
        this.occurredAt = Instant.now();
    }

    protected AbstractDomainEvent(Instant occurredAt) {
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

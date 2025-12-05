package com.estim.javaapi.infrastructure.events;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Outbox event record to be stored in a database table for reliable
 * cross-service event delivery.
 *
 * In a real app, you would annotate this with JPA/Hibernate annotations.
 */
public class OutboxEvent {

    private final UUID id;
    private final String type;
    private final String payload;
    private final Instant occurredAt;
    private final Instant createdAt;
    private boolean processed;

    public OutboxEvent(UUID id,
                       String type,
                       String payload,
                       Instant occurredAt,
                       Instant createdAt,
                       boolean processed) {

        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.payload = Objects.requireNonNull(payload);
        this.occurredAt = Objects.requireNonNull(occurredAt);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.processed = processed;
    }

    public static OutboxEvent fromEnvelope(ExternalEventEnvelope envelope) {
        Instant now = Instant.now();
        return new OutboxEvent(
            UUID.randomUUID(),
            envelope.type(),
            envelope.payload(),
            envelope.occurredAt(),
            now,
            false
        );
    }

    public UUID id() {
        return id;
    }

    public String type() {
        return type;
    }

    public String payload() {
        return payload;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean processed() {
        return processed;
    }

    public void markProcessed() {
        this.processed = true;
    }
}

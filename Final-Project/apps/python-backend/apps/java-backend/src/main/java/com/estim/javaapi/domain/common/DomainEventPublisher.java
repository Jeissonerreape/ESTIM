package com.estim.javaapi.domain.common;

import java.util.List;
import java.util.Objects;

/**
 * Abstraction for publishing domain events produced by aggregates.
 *
 * Application services call this after performing operations on aggregates.
 * Infrastructure implements it using Spring events, an outbox, message bus, etc.
 */
public interface DomainEventPublisher {

    /**
     * Publish a single domain event.
     */
    void publish(DomainEvent event);

    /**
     * Publish multiple domain events.
     */
    default void publishAll(List<? extends DomainEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        events.forEach(this::publish);
    }
}

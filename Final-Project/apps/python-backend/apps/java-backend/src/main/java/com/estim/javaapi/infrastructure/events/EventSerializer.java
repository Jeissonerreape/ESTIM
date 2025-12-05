package com.estim.javaapi.infrastructure.events;

import com.estim.javaapi.domain.common.DomainEvent;

/**
 * Serializes domain events into an envelope suitable for external transport
 * (Kafka, outbox table, etc.).
 */
public interface EventSerializer {

    ExternalEventEnvelope serialize(DomainEvent event);
}

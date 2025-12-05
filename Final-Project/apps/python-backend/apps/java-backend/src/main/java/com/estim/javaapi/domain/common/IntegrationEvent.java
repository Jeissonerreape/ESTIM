package com.estim.javaapi.domain.common;

import java.time.Instant;

/**
 * Marker interface for events that are meant to be published
 * outside this bounded context through an integration event bus
 * (Kafka, RabbitMQ, a Worker Queue, etc).
 */
public interface IntegrationEvent extends DomainEvent {

    @Override
    Instant occurredAt();
}

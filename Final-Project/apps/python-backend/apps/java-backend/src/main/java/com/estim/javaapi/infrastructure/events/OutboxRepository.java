package com.estim.javaapi.infrastructure.events;

import java.util.List;

/**
 * Repository for persisting OutboxEvent records.
 * Implementation will live in the persistence layer (JPA/JDBC).
 */
public interface OutboxRepository {

    void save(OutboxEvent event);

    default void saveAll(List<OutboxEvent> events) {
        for (OutboxEvent e : events) {
            save(e);
        }
    }
}

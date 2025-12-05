package com.estim.javaapi.infrastructure.events;

import com.estim.javaapi.application.handlers.AuditLogOnUserLoggedIn;
import com.estim.javaapi.application.handlers.SendPasswordResetEmailOnRequested;
import com.estim.javaapi.application.handlers.SendWelcomeEmailOnUserRegistered;
import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.events.PasswordResetRequested;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import com.estim.javaapi.domain.user.events.UserRegistered;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Simple in-memory event bus that dispatches domain events
 * to application-level event handlers.
 */
@Component
public class SimpleEventBus implements DomainEventPublisher {

    private final SendWelcomeEmailOnUserRegistered welcomeHandler;
    private final SendPasswordResetEmailOnRequested resetHandler;
    private final AuditLogOnUserLoggedIn auditHandler;

    public SimpleEventBus(SendWelcomeEmailOnUserRegistered welcomeHandler,
                          SendPasswordResetEmailOnRequested resetHandler,
                          AuditLogOnUserLoggedIn auditHandler) {

        this.welcomeHandler = Objects.requireNonNull(welcomeHandler);
        this.resetHandler = Objects.requireNonNull(resetHandler);
        this.auditHandler = Objects.requireNonNull(auditHandler);
    }

    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        if (event instanceof UserRegistered e) {
            welcomeHandler.handle(e);
        } else if (event instanceof PasswordResetRequested e) {
            resetHandler.handle(e);
        } else if (event instanceof UserLoggedIn e) {
            auditHandler.handle(e);
        }
        // You can extend this with more event types as needed
    }

    @Override
    public void publishAll(List<? extends DomainEvent> events) {
        DomainEventPublisher.super.publishAll(events);
    }
}

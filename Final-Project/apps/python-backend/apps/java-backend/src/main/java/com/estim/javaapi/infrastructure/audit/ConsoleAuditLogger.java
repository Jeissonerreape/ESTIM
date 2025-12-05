package com.estim.javaapi.infrastructure.audit;

import com.estim.javaapi.application.handlers.AuditLogger;
import org.springframework.stereotype.Component;

/**
 * Simple AuditLogger implementation that writes to stdout.
 * Replace with a proper logging framework and/or database in production.
 */
@Component
public class ConsoleAuditLogger implements AuditLogger {

    @Override
    public void log(String message) {
        System.out.println("[AUDIT] " + message);
    }
}

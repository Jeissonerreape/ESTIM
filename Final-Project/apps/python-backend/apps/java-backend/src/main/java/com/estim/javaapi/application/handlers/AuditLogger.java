package com.estim.javaapi.application.handlers;

/**
 * Port for writing audit logs of important user actions.
 */
public interface AuditLogger {

    void log(String message);
}

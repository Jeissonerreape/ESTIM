package com.estim.javaapi.application.handlers;

import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Application event handler that writes an audit log entry
 * whenever a user successfully logs in.
 */
@Component
public class AuditLogOnUserLoggedIn {

    private final UserRepository userRepository;
    private final AuditLogger auditLogger;

    public AuditLogOnUserLoggedIn(UserRepository userRepository,
                                  AuditLogger auditLogger) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.auditLogger = Objects.requireNonNull(auditLogger);
    }

    public void handle(UserLoggedIn event) {
        var userOpt = userRepository.findById(event.userId());
        var user = userOpt.orElse(null);

        String email = user != null ? user.email().value() : "unknown";
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(event.occurredAt());

        String message = "User logged in: userId=%s, email=%s, at=%s"
            .formatted(event.userId(), email, timestamp);

        auditLogger.log(message);
    }
}

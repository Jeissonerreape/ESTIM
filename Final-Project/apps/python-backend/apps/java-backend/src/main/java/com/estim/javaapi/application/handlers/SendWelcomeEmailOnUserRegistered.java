package com.estim.javaapi.application.handlers;

import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.UserRegistered;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Application event handler that sends a welcome email when a user registers.
 */
@Component
public class SendWelcomeEmailOnUserRegistered {

    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public SendWelcomeEmailOnUserRegistered(UserRepository userRepository,
                                            EmailSender emailSender) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.emailSender = Objects.requireNonNull(emailSender);
    }

    public void handle(UserRegistered event) {
        var user = userRepository.findById(event.userId())
            .orElse(null);

        if (user == null) {
            return; // user deleted or inconsistent state; silently ignore for now
        }

        String to = user.email().value();
        String subject = "Welcome to ESTIM!";
        String body = """
                Hi %s,
                                
                Welcome to ESTIM! Your account has been created successfully.
                                
                Happy gaming!
                """.formatted(user.profile() != null ? user.profile().displayName() : "there");

        emailSender.send(to, subject, body);
    }
}

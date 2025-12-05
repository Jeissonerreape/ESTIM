package com.estim.javaapi.application.handlers;

import com.estim.javaapi.domain.user.PasswordResetTokenRepository;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.PasswordResetRequested;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Application event handler that sends a password reset email
 * when a PasswordResetRequested event is raised.
 */
@Component
public class SendPasswordResetEmailOnRequested {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final String resetBaseUrl; // e.g. https://estim.com/reset-password?token=

    public SendPasswordResetEmailOnRequested(
        UserRepository userRepository,
        PasswordResetTokenRepository tokenRepository,
        EmailSender emailSender,
        @Value("${security.password-reset.reset-base-url:http://localhost:5173/reset-password?token=}")
        String resetBaseUrl
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.tokenRepository = Objects.requireNonNull(tokenRepository);
        this.emailSender = Objects.requireNonNull(emailSender);
        this.resetBaseUrl = Objects.requireNonNull(resetBaseUrl);
    }

    public void handle(PasswordResetRequested event) {
        var tokenOpt = tokenRepository.findByTokenId(event.tokenId());
        if (tokenOpt.isEmpty()) {
            return;
        }

        var token = tokenOpt.get();

        var userOpt = userRepository.findById(token.userId());
        if (userOpt.isEmpty()) {
            return;
        }

        var user = userOpt.get();

        String to = user.email().value();
        String resetLink = resetBaseUrl + token.token();

        String subject = "Password reset request";
        String body = """
                Hi %s,
                                
                We received a request to reset your password.
                You can reset it using the following link:
                %s
                                
                If you did not request this, you can safely ignore this email.
                """.formatted(user.profile() != null ? user.profile().displayName() : "there",
            resetLink);

        emailSender.send(to, subject, body);
    }
}

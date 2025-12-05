package com.estim.javaapi.application.password;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.PasswordResetToken;
import com.estim.javaapi.domain.user.PasswordResetTokenRepository;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.PasswordResetRequested;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Application service for initiating the password reset flow.
 */
@Service
public class RequestPasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetTokenGenerator tokenGenerator;
    private final DomainEventPublisher eventPublisher;
    private final Duration tokenTtl;

    public RequestPasswordResetService(
        UserRepository userRepository,
        PasswordResetTokenRepository tokenRepository,
        PasswordResetTokenGenerator tokenGenerator,
        DomainEventPublisher eventPublisher,
        @Value("${security.password-reset.token-ttl:PT1H}") Duration tokenTtl
    ) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.tokenRepository = Objects.requireNonNull(tokenRepository);
        this.tokenGenerator = Objects.requireNonNull(tokenGenerator);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.tokenTtl = Objects.requireNonNull(tokenTtl);
    }

    public void requestReset(RequestPasswordResetCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
            .orElse(null);

        if (user == null) {
            // silently ignore to avoid leaking which emails exist
            return;
        }

        String rawToken = tokenGenerator.generate();
        Instant expiresAt = Instant.now().plus(tokenTtl);

        PasswordResetToken resetToken = PasswordResetToken.create(
            user.id(),
            rawToken,
            expiresAt
        );

        PasswordResetToken savedToken = tokenRepository.save(resetToken);

        eventPublisher.publish(
            new PasswordResetRequested(user.id(), savedToken.id(), Instant.now())
        );
    }
}

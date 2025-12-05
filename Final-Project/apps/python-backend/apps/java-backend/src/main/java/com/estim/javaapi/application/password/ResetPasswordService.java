package com.estim.javaapi.application.password;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.application.auth.PasswordHasher;
import com.estim.javaapi.application.auth.PasswordPolicy;
import com.estim.javaapi.domain.user.PasswordResetToken;
import com.estim.javaapi.domain.user.PasswordResetTokenRepository;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.PasswordChanged;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

/**
 * Application service for completing the password reset flow.
 */
@Service
public class ResetPasswordService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public ResetPasswordService(PasswordResetTokenRepository tokenRepository,
                                UserRepository userRepository,
                                PasswordPolicy passwordPolicy,
                                PasswordHasher passwordHasher,
                                DomainEventPublisher eventPublisher) {

        this.tokenRepository = Objects.requireNonNull(tokenRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordPolicy = Objects.requireNonNull(passwordPolicy);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void resetPassword(ResetPasswordCommand command) {
        PasswordResetToken resetToken = tokenRepository.findByToken(command.token())
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.used() || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        if (!passwordPolicy.isValid(command.newPassword())) {
            throw new IllegalArgumentException("Password does not meet policy requirements");
        }

        User user = userRepository.findById(resetToken.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Assumes User has a method like changePassword(PasswordHash)
        user.changePassword(passwordHasher.hash(command.newPassword()));

        resetToken.markUsed();

        userRepository.save(user);
        tokenRepository.save(resetToken);

        eventPublisher.publish(new PasswordChanged(user.id(), Instant.now()));
    }
}

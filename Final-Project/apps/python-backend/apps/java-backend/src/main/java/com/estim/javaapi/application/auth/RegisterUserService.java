package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for registering a new user.
 */
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public RegisterUserService(UserRepository userRepository,
                               PasswordPolicy passwordPolicy,
                               PasswordHasher passwordHasher,
                               DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordPolicy = Objects.requireNonNull(passwordPolicy);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public User register(RegisterUserCommand command) {
        // ---- Basic null/blank validation ----
        validateCommand(command);

        // ---- Email validation (null/blank + @ + dot after @) via Email.of ----
        Email email = Email.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        // ---- Username/displayName rules & uniqueness ----
        String displayName = command.displayName().trim();
        validateDisplayName(displayName);

        if (userRepository.existsByDisplayName(displayName)) {
            throw new IllegalArgumentException("Display name already in use");
        }

        // ---- Password policy ----
        if (!passwordPolicy.isValid(command.password())) {
            throw new IllegalArgumentException(passwordPolicy.description());
        }

        var hashedPassword = passwordHasher.hash(command.password());

        // Create a new UserId
        UserId id = new UserId(UUID.randomUUID());

        // Default privacy settings
        PrivacySettings privacySettings = new PrivacySettings(
            true,  // showProfile
            true,  // showActivity
            true   // showWishlist
        );

        // Initial profile with displayName + default privacy; avatar/bio/location null for now
        UserProfile initialProfile = new UserProfile(
            displayName,
            null,   // avatarUrl
            null,   // bio
            null,   // location
            privacySettings
        );

        // Use the aggregate factory; factory sets initial status + events
        User user = User.register(
            id,
            email,
            hashedPassword,
            initialProfile
        );

        User saved = userRepository.save(user);

        // Publish all domain events raised by the aggregate, then clear them
        eventPublisher.publishAll(saved.domainEvents());
        saved.clearDomainEvents();

        return saved;
    }

    // ---------- Validation helpers ----------

    private void validateCommand(RegisterUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        if (command.displayName() == null || command.displayName().isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }
    }

    /**
     * Python-like identifier rules for username:
     * - No spaces
     * - Must match: ^[A-Za-z_][A-Za-z0-9_]*$
     */
    private void validateDisplayName(String displayName) {
        if (displayName.contains(" ")) {
            throw new IllegalArgumentException("Display name must not contain spaces");
        }

        // First char: letter or underscore; rest: letters, digits, underscore
        if (!displayName.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
            throw new IllegalArgumentException(
                "Display name must start with a letter or underscore and contain only letters, digits, or underscores"
            );
        }
    }
}

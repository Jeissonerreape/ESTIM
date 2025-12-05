package com.estim.javaapi.application.profile;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for updating the current user's profile.
 *
 * It supports partial updates: any null field in the command is interpreted
 * as "keep existing value".
 */
@Component
public class UpdateUserProfileService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public UpdateUserProfileService(UserRepository userRepository,
                                    DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void updateProfile(UpdateUserProfileCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile existingProfile = user.profile();
        PrivacySettings existingPrivacy = existingProfile != null
            ? existingProfile.privacySettings()
            : new PrivacySettings(true, true, true); // sane default if null

        // --- Merge fields ---

        // Display name: prefer command, then existing profile; if still null, it's an invariant violation
        String displayName;
        if (command.displayName() != null) {
            displayName = command.displayName();
        } else if (existingProfile != null && existingProfile.displayName() != null) {
            displayName = existingProfile.displayName();
        } else {
            throw new IllegalStateException("User profile has no display name");
        }

        String avatarUrl = command.avatarUrl() != null
            ? command.avatarUrl()
            : existingProfile != null ? existingProfile.avatarUrl() : null;

        String bio = command.bio() != null
            ? command.bio()
            : existingProfile != null ? existingProfile.bio() : null;

        String location = command.location() != null
            ? command.location()
            : existingProfile != null ? existingProfile.location() : null;

        boolean showProfile = command.showProfile() != null
            ? command.showProfile()
            : existingPrivacy.showProfile();

        boolean showActivity = command.showActivity() != null
            ? command.showActivity()
            : existingPrivacy.showActivity();

        boolean showWishlist = command.showWishlist() != null
            ? command.showWishlist()
            : existingPrivacy.showWishlist();

        PrivacySettings newPrivacy = new PrivacySettings(
            showProfile,
            showActivity,
            showWishlist
        );

        UserProfile newProfile = new UserProfile(
            displayName,
            avatarUrl,
            bio,
            location,
            newPrivacy
        );

        user.updateProfile(newProfile);

        userRepository.save(user);

        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}

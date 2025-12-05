package com.estim.javaapi.application.profile;

import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service to retrieve a user's profile,
 * applying privacy rules based on the requester.
 */
@Service
public class GetUserProfileService {

    private final UserRepository userRepository;

    public GetUserProfileService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public User getProfile(GetUserProfileQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        UserId targetId = new UserId(UUID.fromString(query.targetUserId()));
        User user = userRepository.findById(targetId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = user.profile();
        if (profile == null) {
            throw new IllegalArgumentException("User profile not found");
        }

        PrivacySettings privacy = profile.privacySettings();
        boolean isSelf = query.requesterUserId().equals(query.targetUserId());

        // Simple privacy rule: if not self and profile is not visible, deny
        if (!isSelf && (privacy == null || !privacy.showProfile())) {
            throw new IllegalStateException("Profile is not public");
        }

        // Return the full User aggregate; the controller/mapper will shape the DTO
        return user;
    }
}

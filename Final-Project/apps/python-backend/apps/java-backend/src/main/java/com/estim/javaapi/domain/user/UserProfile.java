package com.estim.javaapi.domain.user;

import java.util.Objects;

/**
 * Immutable value object representing a user's profile data.
 */
public final class UserProfile {

    private final String displayName;
    private final String avatarUrl;
    private final String bio;
    private final String location;
    private final PrivacySettings privacySettings;

    public UserProfile(
        String displayName,
        String avatarUrl,
        String bio,
        String location,
        PrivacySettings privacySettings
    ) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.location = location;
        this.privacySettings = privacySettings;
    }

    public String displayName() {
        return displayName;
    }

    public String avatarUrl() {
        return avatarUrl;
    }

    public String bio() {
        return bio;
    }

    public String location() {
        return location;
    }

    public PrivacySettings privacySettings() {
        return privacySettings;
    }

    /**
     * Creates a new UserProfile with updated fields.
     * Useful for partial updates (immutability preserved).
     */
    public UserProfile with(
        String displayName,
        String avatarUrl,
        String bio,
        String location,
        PrivacySettings privacySettings
    ) {
        return new UserProfile(
            displayName != null ? displayName : this.displayName,
            avatarUrl != null ? avatarUrl : this.avatarUrl,
            bio != null ? bio : this.bio,
            location != null ? location : this.location,
            privacySettings != null ? privacySettings : this.privacySettings
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile other)) return false;
        return Objects.equals(displayName, other.displayName)
            && Objects.equals(avatarUrl, other.avatarUrl)
            && Objects.equals(bio, other.bio)
            && Objects.equals(location, other.location)
            && Objects.equals(privacySettings, other.privacySettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, avatarUrl, bio, location, privacySettings);
    }
}

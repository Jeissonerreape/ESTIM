package com.estim.javaapi.domain.user;

import java.util.Objects;

/**
 * Immutable value object representing a user's privacy configuration.
 */
public final class PrivacySettings {

    private final boolean showProfile;
    private final boolean showActivity;
    private final boolean showWishlist;

    public PrivacySettings(
        boolean showProfile,
        boolean showActivity,
        boolean showWishlist
    ) {
        this.showProfile = showProfile;
        this.showActivity = showActivity;
        this.showWishlist = showWishlist;
    }

    public boolean showProfile() {
        return showProfile;
    }

    public boolean showActivity() {
        return showActivity;
    }

    public boolean showWishlist() {
        return showWishlist;
    }

    /**
     * Creates a new PrivacySettings object while keeping existing values for null parameters.
     * (Useful for partial updates.)
     */
    public PrivacySettings with(
        Boolean showProfile,
        Boolean showActivity,
        Boolean showWishlist
    ) {
        return new PrivacySettings(
            showProfile != null ? showProfile : this.showProfile,
            showActivity != null ? showActivity : this.showActivity,
            showWishlist != null ? showWishlist : this.showWishlist
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrivacySettings other)) return false;
        return showProfile == other.showProfile
            && showActivity == other.showActivity
            && showWishlist == other.showWishlist;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showProfile, showActivity, showWishlist);
    }

    @Override
    public String toString() {
        return "PrivacySettings{" +
            "showProfile=" + showProfile +
            ", showActivity=" + showActivity +
            ", showWishlist=" + showWishlist +
            '}';
    }
}

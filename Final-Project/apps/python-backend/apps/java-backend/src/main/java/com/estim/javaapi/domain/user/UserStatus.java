package com.estim.javaapi.domain.user;

/**
 * Represents the lifecycle status of a user account in the system.
 *
 * This is intentionally richer than just "active / banned" so that
 * admin & moderation flows (FR-14), registration (FR-01), and future
 * features (e.g. soft-deletion) have clear semantics.
 */
public enum UserStatus {

    /**
     * User has registered but has not yet completed required activation
     * steps (e.g., email verification).
     */
    PENDING_ACTIVATION,

    /**
     * User is fully active and allowed to log in and use the platform.
     */
    ACTIVE,

    /**
     * User account is temporarily suspended (e.g., due to disputes, fraud checks).
     * Typically cannot log in or make purchases until reinstated.
     */
    SUSPENDED,

    /**
     * User account is permanently banned. Cannot log in or use any features.
     */
    BANNED,

    /**
     * User account has been soft-deleted.
     * Usually hidden from UI but may still exist for audit/compliance.
     */
    DELETED;

    /**
     * Returns true if a user in this status is allowed to authenticate
     * (i.e., successfully log in and obtain tokens).
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /**
     * Returns true if a user in this status is allowed to perform purchases
     * or other sensitive operations.
     */
    public boolean canPerformPurchases() {
        return this == ACTIVE;
    }

    /**
     * Returns true if this status represents some kind of blocked account
     * (moderated, banned, removed, etc.).
     */
    public boolean isBlocked() {
        return this == SUSPENDED || this == BANNED || this == DELETED;
    }
}

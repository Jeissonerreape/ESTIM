package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.UserStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    // Profile fields
    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "bio", length = 2000)
    private String bio;

    @Column(name = "location", length = 100)
    private String location;

    // Privacy settings
    @Column(name = "privacy_show_profile", nullable = false)
    private boolean privacyShowProfile = true;

    @Column(name = "privacy_show_activity", nullable = false)
    private boolean privacyShowActivity = true;

    @Column(name = "privacy_show_wishlist", nullable = false)
    private boolean privacyShowWishlist = true;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<PaymentMethodJpaEntity> paymentMethods = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<OAuthAccountJpaEntity> linkedAccounts = new ArrayList<>();

    protected UserJpaEntity() {
        // for JPA
    }

    // Getters & setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isPrivacyShowProfile() {
        return privacyShowProfile;
    }

    public void setPrivacyShowProfile(boolean privacyShowProfile) {
        this.privacyShowProfile = privacyShowProfile;
    }

    public boolean isPrivacyShowActivity() {
        return privacyShowActivity;
    }

    public void setPrivacyShowActivity(boolean privacyShowActivity) {
        this.privacyShowActivity = privacyShowActivity;
    }

    public boolean isPrivacyShowWishlist() {
        return privacyShowWishlist;
    }

    public void setPrivacyShowWishlist(boolean privacyShowWishlist) {
        this.privacyShowWishlist = privacyShowWishlist;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public List<PaymentMethodJpaEntity> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodJpaEntity> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<OAuthAccountJpaEntity> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<OAuthAccountJpaEntity> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }
}

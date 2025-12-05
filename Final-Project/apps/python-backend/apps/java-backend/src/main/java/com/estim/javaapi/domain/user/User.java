package com.estim.javaapi.domain.user;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.events.UserBanned;
import com.estim.javaapi.domain.user.events.UserEmailVerified;
import com.estim.javaapi.domain.user.events.UserProfileUpdated;
import com.estim.javaapi.domain.user.events.UserRegistered;
import com.estim.javaapi.domain.user.events.UserUnbanned;
import com.estim.javaapi.domain.user.events.OAuthAccountLinked;
import com.estim.javaapi.domain.user.events.PaymentMethodAdded;
import com.estim.javaapi.domain.user.events.PaymentMethodRemoved;
import com.estim.javaapi.domain.user.events.PasswordChanged;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root that represents an application user.
 */
public class User {

    private final UserId id;
    private final Email email;

    private PasswordHash passwordHash;
    private UserStatus status;
    private boolean emailVerified;
    private UserProfile profile;

    private final List<PaymentMethod> paymentMethods;
    private final List<OAuthAccount> linkedAccounts;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt; // optional, might be used later

    // Domain events raised by this aggregate instance
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ---------- Constructors / Factory ----------

    private User(
        UserId id,
        Email email,
        PasswordHash passwordHash,
        UserStatus status,
        boolean emailVerified,
        UserProfile profile,
        List<PaymentMethod> paymentMethods,
        List<OAuthAccount> linkedAccounts,
        Instant createdAt,
        Instant updatedAt,
        Instant lastLoginAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.emailVerified = emailVerified;
        this.profile = profile;
        this.paymentMethods = paymentMethods != null ? new ArrayList<>(paymentMethods) : new ArrayList<>();
        this.linkedAccounts = linkedAccounts != null ? new ArrayList<>(linkedAccounts) : new ArrayList<>();
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * Factory method used when registering a brand-new user via email/password.
     */
    public static User register(UserId id, Email email, PasswordHash passwordHash, UserProfile initialProfile) {
        Instant now = Instant.now();

        User user = new User(
            id,
            email,
            passwordHash,
            UserStatus.ACTIVE, // or PENDING_ACTIVATION if you add that status
            false,             // email not verified yet
            initialProfile,
            List.of(),
            List.of(),
            now,
            now,
            null
        );

        user.registerEvent(new UserRegistered(id, email.value(), now));
        return user;
    }

    // ---------- Domain behavior ----------

    /**
     * Marks the user's email as verified (idempotent).
     */
    public void verifyEmail() {
        if (this.emailVerified) {
            return;
        }
        this.emailVerified = true;
        touchUpdated();
        registerEvent(new UserEmailVerified(this.id, Instant.now()));
    }

    /**
     * Bans the user (idempotent).
     */
    public void ban() {
        if (this.status == UserStatus.BANNED) {
            return;
        }
        this.status = UserStatus.BANNED;
        touchUpdated();
        registerEvent(new UserBanned(this.id, Instant.now()));
    }

    /**
     * Unbans the user (idempotent) – typically sets back to ACTIVE.
     */
    public void unban() {
        if (this.status != UserStatus.BANNED) {
            return;
        }
        this.status = UserStatus.ACTIVE;
        touchUpdated();
        registerEvent(new UserUnbanned(this.id, Instant.now()));
    }

    /**
     * Updates the user profile based on a profile update object.
     * The semantics of applying the update are delegated to UserProfileUpdate.
     */
    public void updateProfile(UserProfile newProfile) {
        Objects.requireNonNull(newProfile, "newProfile must not be null");
        this.profile = newProfile;
        touchUpdated();
        registerEvent(new UserProfileUpdated(this.id, Instant.now()));
    }


    /**
     * Adds a payment method to the user (if it's not already present).
     */
    public void addPaymentMethod(PaymentMethod method) {
        Objects.requireNonNull(method, "payment method must not be null");

        boolean alreadyExists = this.paymentMethods.stream()
            .anyMatch(pm -> pm.id().equals(method.id()));
        if (alreadyExists) {
            return;
        }

        this.paymentMethods.add(method);
        touchUpdated();
        registerEvent(new PaymentMethodAdded(this.id, method.id(), Instant.now()));
    }

    /**
     * Removes a payment method by id (if it exists).
     */
    public void removePaymentMethod(PaymentMethodId id) {
        Objects.requireNonNull(id, "paymentMethodId must not be null");

        boolean removed = this.paymentMethods.removeIf(pm -> pm.id().equals(id));
        if (removed) {
            touchUpdated();
            registerEvent(new PaymentMethodRemoved(this.id, id, Instant.now()));
        }
    }

    /**
     * Links an OAuth account to this user (e.g. Google, Steam).
     */
    public void linkOAuthAccount(OAuthAccount account) {
        Objects.requireNonNull(account, "account must not be null");

        boolean alreadyLinked = this.linkedAccounts.stream()
            .anyMatch(acc ->
                acc.provider().equals(account.provider())
                    && acc.externalUserId().equals(account.externalUserId())
            );
        if (alreadyLinked) {
            return;
        }

        this.linkedAccounts.add(account);
        touchUpdated();
        registerEvent(new OAuthAccountLinked(this.id, account.provider(), account.externalUserId(), Instant.now()));
    }

    // Optional: update last login (e.g. on successful authentication)
    public void markLogin() {
        this.lastLoginAt = Instant.now();
        touchUpdated();
        // could emit a UserLoggedIn event if you define one
    }

    /**
     * Changes the user's password hash.
     * The hashing itself is done outside (in the application layer),
     * this method only updates the aggregate and raises a domain event.
     */
    public void changePassword(PasswordHash newPasswordHash) {
        Objects.requireNonNull(newPasswordHash, "newPasswordHash must not be null");

        // Optionally short-circuit if same hash
        if (this.passwordHash.equals(newPasswordHash)) {
            return;
        }

        this.passwordHash = newPasswordHash;
        touchUpdated();
        registerEvent(new PasswordChanged(this.id, Instant.now()));
    }

    // ---------- Domain events support ----------

    private void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    private void touchUpdated() {
        this.updatedAt = Instant.now();
    }

    /**
     * Returns an immutable view of the domain events raised by this aggregate.
     */
    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clears all accumulated domain events. Typically used by the persistence layer
     * once events have been published.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // ---------- Getters (no setters: aggregate invariants must go through behavior) ----------

    public UserId id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public UserStatus status() {
        return status;
    }

    public boolean emailVerified() {
        return emailVerified;
    }

    public UserProfile profile() {
        return profile;
    }

    public List<PaymentMethod> paymentMethods() {
        return Collections.unmodifiableList(paymentMethods);
    }

    public List<OAuthAccount> linkedAccounts() {
        return Collections.unmodifiableList(linkedAccounts);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant lastLoginAt() {
        return lastLoginAt;
    }

    // ---------- Equality based on identity ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

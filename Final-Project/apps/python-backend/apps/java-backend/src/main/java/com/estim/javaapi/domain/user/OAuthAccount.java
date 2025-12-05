package com.estim.javaapi.domain.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an OAuth account linked to a user identity.
 * Stores provider type, the external user ID, email, and link timestamp.
 */
public class OAuthAccount {

    private final OAuthAccountId id;
    private final OAuthProvider provider;
    private final String externalUserId;  // e.g., Google sub, Steam ID
    private final String email;           // optional, may be null
    private final Instant linkedAt;

    public OAuthAccount(OAuthAccountId id,
                        OAuthProvider provider,
                        String externalUserId,
                        String email,
                        Instant linkedAt) {

        this.id = Objects.requireNonNull(id);
        this.provider = Objects.requireNonNull(provider);
        this.externalUserId = Objects.requireNonNull(externalUserId);
        this.email = email;
        this.linkedAt = linkedAt != null ? linkedAt : Instant.now();
    }

    public OAuthAccountId id() {
        return id;
    }

    public OAuthProvider provider() {
        return provider;
    }

    public String externalUserId() {
        return externalUserId;
    }

    public String email() {
        return email;
    }

    public Instant linkedAt() {
        return linkedAt;
    }

    public static OAuthAccount create(OAuthProvider provider,
                                      String externalUserId,
                                      String email) {

        return new OAuthAccount(
            new OAuthAccountId(UUID.randomUUID()),
            provider,
            externalUserId,
            email,
            Instant.now()
        );
    }
}

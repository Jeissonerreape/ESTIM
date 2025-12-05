package com.estim.javaapi.infrastructure.persistence.wishlist;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wishlists")
public class WishlistItemJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    protected WishlistItemJpaEntity() {
        // for JPA
    }

    public WishlistItemJpaEntity(UUID userId,
                                 UUID gameId,
                                 Instant addedAt) {
        this.userId = userId;
        this.gameId = gameId;
        this.addedAt = addedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
}

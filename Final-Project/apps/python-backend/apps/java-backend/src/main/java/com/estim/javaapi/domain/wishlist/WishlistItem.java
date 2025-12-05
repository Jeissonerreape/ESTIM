package com.estim.javaapi.domain.wishlist;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Wishlist entry for a (user, game) pair.
 * Identity is the composite (userId, gameId).
 */
public final class WishlistItem {

    private final UserId userId;
    private final GameId gameId;
    private final Instant addedAt;
    /**
     * Snapshot of the price when the user added the game to the wishlist.
     * Optional: can be null if not captured.
     */
    private final BigDecimal priceWhenAdded;

    private final Map<String, Boolean> notificationPreferences;

    private WishlistItem(UserId userId,
                         GameId gameId,
                         Instant addedAt,
                         BigDecimal priceWhenAdded,
                         Map<String, Boolean> notificationPreferences) {

        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
        this.addedAt = Objects.requireNonNull(addedAt, "addedAt must not be null");
        this.priceWhenAdded = priceWhenAdded; // optional

        // store as unmodifiable to keep immutability
        this.notificationPreferences = notificationPreferences == null
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(notificationPreferences);
    }

    /**
     * Factory method using current time and no price snapshot.
     */
    public static WishlistItem newItem(UserId userId, GameId gameId) {
        return new WishlistItem(userId, gameId, Instant.now(), null, Collections.emptyMap());
    }

    public static WishlistItem of(UserId userId,
                                  GameId gameId,
                                  Instant addedAt,
                                  BigDecimal priceWhenAdded,
                                  Map<String, Boolean> notificationPreferences) {

        return new WishlistItem(userId, gameId, addedAt, priceWhenAdded, notificationPreferences);
    }

    public WishlistItem withNotificationPreferences(Map<String, Boolean> newPreferences) {
        return new WishlistItem(
            this.userId,
            this.gameId,
            this.addedAt,
            this.priceWhenAdded,
            newPreferences
        );
    }

    public Map<String, Boolean> getNotificationPreferences() {
        return notificationPreferences;
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public BigDecimal getPriceWhenAdded() {
        return priceWhenAdded;
    }

    public boolean hasPreference(String key) {
        return notificationPreferences.getOrDefault(key, false);
    }

    /**
     * Identity is the composite (userId, gameId).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistItem that)) return false;
        return userId.equals(that.userId) &&
            gameId.equals(that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, gameId);
    }

    @Override
    public String toString() {
        return "WishlistItem{" +
            "userId=" + userId +
            ", gameId=" + gameId +
            ", addedAt=" + addedAt +
            ", priceWhenAdded=" + priceWhenAdded +
            ", notificationPreferences=" + notificationPreferences +
            '}';
    }

}

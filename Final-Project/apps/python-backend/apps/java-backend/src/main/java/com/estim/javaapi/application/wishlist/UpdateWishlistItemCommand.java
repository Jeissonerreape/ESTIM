package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Map;
import java.util.Objects;

/**
 * Command to update settings for a wishlist item, such as notification preferences.
 *
 * The structure of notificationPreferences is intentionally generic for now.
 * Example keys: "notifyOnAnyDiscount", "notifyOnSale", etc.
 * This can be replaced later with a dedicated value object.
 */
public final class UpdateWishlistItemCommand {

    private final UserId userId;
    private final GameId gameId;
    private final Map<String, Boolean> notificationPreferences;

    public UpdateWishlistItemCommand(UserId userId,
                                     GameId gameId,
                                     Map<String, Boolean> notificationPreferences) {

        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
        this.notificationPreferences = Objects.requireNonNull(notificationPreferences, "notificationPreferences must not be null");
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    public Map<String, Boolean> getNotificationPreferences() {
        return notificationPreferences;
    }
}

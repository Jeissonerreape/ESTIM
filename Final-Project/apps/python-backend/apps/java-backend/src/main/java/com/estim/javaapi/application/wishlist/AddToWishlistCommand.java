package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Objects;

/**
 * Command to add a game to the user's wishlist.
 */
public final class AddToWishlistCommand {

    private final UserId userId;
    private final GameId gameId;

    public AddToWishlistCommand(UserId userId, GameId gameId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }
}

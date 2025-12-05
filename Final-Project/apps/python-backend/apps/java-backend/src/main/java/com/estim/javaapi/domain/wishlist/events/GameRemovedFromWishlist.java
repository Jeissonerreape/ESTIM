package com.estim.javaapi.domain.wishlist.events;

import com.estim.javaapi.domain.common.AbstractDomainEvent;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Objects;

/**
 * Domain event fired when a user removes a game from their wishlist.
 */
public final class GameRemovedFromWishlist extends AbstractDomainEvent {

    private final UserId userId;
    private final GameId gameId;

    public GameRemovedFromWishlist(UserId userId, GameId gameId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "GameRemovedFromWishlist{" +
            "userId=" + userId +
            ", gameId=" + gameId +
            '}';
    }
}

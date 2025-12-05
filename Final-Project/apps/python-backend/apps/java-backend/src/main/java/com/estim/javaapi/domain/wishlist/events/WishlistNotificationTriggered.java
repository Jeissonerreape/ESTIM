package com.estim.javaapi.domain.wishlist.events;

import com.estim.javaapi.domain.common.AbstractDomainEvent;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain event fired when conditions to notify a user about a wishlist item
 * (e.g., a price drop or sale) have been met.
 *
 * Typically emitted by a pricing or promotion handler that reacts to changes
 * in the catalog/pricing service.
 */
public final class WishlistNotificationTriggered extends AbstractDomainEvent {

    private final UserId userId;
    private final GameId gameId;

    /**
     * Previous known price for this game for this user (may be null if unknown).
     */
    private final BigDecimal previousPrice;

    /**
     * Current price of the game when the notification is triggered.
     */
    private final BigDecimal currentPrice;

    /**
     * Discount percentage (0–100) if applicable, may be null when not relevant.
     */
    private final BigDecimal discountPercentage;

    public WishlistNotificationTriggered(UserId userId,
                                         GameId gameId,
                                         BigDecimal previousPrice,
                                         BigDecimal currentPrice,
                                         BigDecimal discountPercentage) {

        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
        this.previousPrice = previousPrice;           // optional
        this.currentPrice = currentPrice;             // optional, depending on pricing integration
        this.discountPercentage = discountPercentage; // optional
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    public BigDecimal getPreviousPrice() {
        return previousPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return "WishlistNotificationTriggered{" +
            "userId=" + userId +
            ", gameId=" + gameId +
            ", previousPrice=" + previousPrice +
            ", currentPrice=" + currentPrice +
            ", discountPercentage=" + discountPercentage +
            '}';
    }
}

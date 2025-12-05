package com.estim.javaapi.application.handlers;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import com.estim.javaapi.domain.wishlist.events.WishlistNotificationTriggered;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Handler that reacts to pricing events (e.g., game goes on sale)
 * and triggers notifications for all users who have that game on
 * their wishlist.
 *
 * This class is intentionally generic: some external adapter
 * (Kafka listener, HTTP webhook, etc.) should call
 * {@link #handleGameOnSale(GameId, BigDecimal, BigDecimal, BigDecimal)}
 * when a pricing event is received.
 */
@Component
public class WishlistPricingNotificationHandler {

    private final WishlistRepository wishlistRepository;
    private final DomainEventPublisher eventPublisher;

    public WishlistPricingNotificationHandler(WishlistRepository wishlistRepository,
                                              DomainEventPublisher eventPublisher) {
        this.wishlistRepository = wishlistRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Handle a "game on sale" situation.
     *
     * @param gameId             The game that is on sale.
     * @param previousPrice      Previous catalog price (may be null).
     * @param currentPrice       Current price (should be non-null).
     * @param discountPercentage Discount percentage (0–100), may be null if not available.
     */
    public void handleGameOnSale(GameId gameId,
                                 BigDecimal previousPrice,
                                 BigDecimal currentPrice,
                                 BigDecimal discountPercentage) {

        Objects.requireNonNull(gameId, "gameId must not be null");
        Objects.requireNonNull(currentPrice, "currentPrice must not be null");

        // 1. Find all wishlist entries for this game
        List<WishlistItem> wishlistItems = wishlistRepository.findByGameId(gameId);

        if (wishlistItems.isEmpty()) {
            // Nobody has this game in wishlist → nothing to do
            return;
        }

        // 2. For each user, publish a WishlistNotificationTriggered event
        for (WishlistItem item : wishlistItems) {
            var userId = item.getUserId();

            // If we don't have a previousPrice from pricing, fall back to the snapshot
            BigDecimal effectivePreviousPrice = previousPrice != null
                ? previousPrice
                : item.getPriceWhenAdded();

            WishlistNotificationTriggered event = new WishlistNotificationTriggered(
                userId,
                gameId,
                effectivePreviousPrice,
                currentPrice,
                discountPercentage
            );

            eventPublisher.publish(event);
        }
    }
}

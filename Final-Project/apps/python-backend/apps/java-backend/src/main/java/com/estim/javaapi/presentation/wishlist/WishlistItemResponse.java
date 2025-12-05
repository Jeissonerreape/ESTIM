package com.estim.javaapi.presentation.wishlist;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Response representation of a wishlist item.
 *
 * currentPrice may be null if catalog/pricing integration
 * is not available for this endpoint yet.
 */
public record WishlistItemResponse(
    String gameId,
    String gameTitle,
    String coverImageUrl,
    Instant addedAt,
    Map<String, Boolean> notificationPreferences,
    BigDecimal currentPrice
) {}


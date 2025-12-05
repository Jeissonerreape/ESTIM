package com.estim.javaapi.presentation.wishlist;

import java.util.Map;

/**
 * Request body for adding/updating a wishlist entry.
 */
public record WishlistItemRequest(
    String gameId,
    Map<String, Boolean> notificationPreferences
) {
}

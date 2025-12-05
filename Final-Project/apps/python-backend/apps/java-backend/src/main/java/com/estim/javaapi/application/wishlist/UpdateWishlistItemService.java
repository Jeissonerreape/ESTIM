package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateWishlistItemService {

    private final WishlistRepository wishlistRepository;

    public UpdateWishlistItemService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public void updateWishlistItem(UpdateWishlistItemCommand command) {
        var userId = command.getUserId();
        var gameId = command.getGameId();
        var newPreferences = command.getNotificationPreferences();

        var existing = wishlistRepository.findByUserIdAndGameId(userId, gameId)
            .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));

        var updated = existing.withNotificationPreferences(newPreferences);

        wishlistRepository.save(updated);
        // If you want, you could publish a "WishlistItemUpdated" event later.
    }
}

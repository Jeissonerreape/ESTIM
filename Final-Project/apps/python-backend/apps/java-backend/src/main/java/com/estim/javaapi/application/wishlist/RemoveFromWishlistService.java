package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import com.estim.javaapi.domain.wishlist.events.GameRemovedFromWishlist;
import org.springframework.stereotype.Service;

@Service
public class RemoveFromWishlistService {

    private final WishlistRepository wishlistRepository;
    private final DomainEventPublisher eventPublisher;

    public RemoveFromWishlistService(WishlistRepository wishlistRepository,
                                     DomainEventPublisher eventPublisher) {
        this.wishlistRepository = wishlistRepository;
        this.eventPublisher = eventPublisher;
    }

    public void removeFromWishlist(RemoveFromWishlistCommand command) {
        var userId = command.getUserId();
        var gameId = command.getGameId();

        var existing = wishlistRepository.findByUserIdAndGameId(userId, gameId)
            .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));

        wishlistRepository.delete(existing);

        eventPublisher.publish(new GameRemovedFromWishlist(userId, gameId));
    }
}

package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.library.LibraryRepository;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import com.estim.javaapi.domain.wishlist.events.GameAddedToWishlist;
import org.springframework.stereotype.Service;

@Service
public class AddToWishlistService {

    private final WishlistRepository wishlistRepository;
    private final LibraryRepository libraryRepository;
    private final DomainEventPublisher eventPublisher;

    public AddToWishlistService(WishlistRepository wishlistRepository,
                                LibraryRepository libraryRepository,
                                DomainEventPublisher eventPublisher) {
        this.wishlistRepository = wishlistRepository;
        this.libraryRepository = libraryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Adds a game to the user's wishlist.
     *
     * Business rules:
     * - The game must NOT already be in the user's library.
     * - The game must NOT already be in the user's wishlist.
     */
    public void addToWishlist(AddToWishlistCommand command) {
        var userId = command.getUserId();
        var gameId = command.getGameId();

        // 1) Block if the game is already in the user's library
        if (libraryRepository.findByUserAndGame(userId, gameId).isPresent()) {
            // You could later replace this with a custom exception type
            // if you want more structured error codes.
            throw new IllegalStateException("Game is already in library");
        }

        // 2) Block duplicates in the wishlist (existing rule)
        if (wishlistRepository.existsByUserIdAndGameId(userId, gameId)) {
            // You can make this a no-op if you prefer idempotency
            throw new IllegalStateException("Game is already in wishlist");
        }

        // 3) Create and persist wishlist item
        WishlistItem item = WishlistItem.newItem(userId, gameId);
        wishlistRepository.save(item);

        // 4) Publish domain event
        eventPublisher.publish(new GameAddedToWishlist(userId, gameId));
    }
}

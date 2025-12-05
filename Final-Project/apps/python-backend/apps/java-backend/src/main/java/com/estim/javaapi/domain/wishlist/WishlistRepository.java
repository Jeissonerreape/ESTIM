package com.estim.javaapi.domain.wishlist;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository {

    List<WishlistItem> findByUserId(UserId userId);

    Optional<WishlistItem> findByUserIdAndGameId(UserId userId, GameId gameId);

    void save(WishlistItem item);

    void delete(WishlistItem item);

    boolean existsByUserIdAndGameId(UserId userId, GameId gameId);

    // 👇 NEW: used by the pricing handler
    List<WishlistItem> findByGameId(GameId gameId);
}

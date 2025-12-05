package com.estim.javaapi.presentation.wishlist;

import com.estim.javaapi.domain.wishlist.WishlistItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WishlistMapper {

    public WishlistItemResponse toResponse(
        WishlistItem item,
        String gameTitle,
        String coverImageUrl,
        BigDecimal currentPrice
    ) {
        return new WishlistItemResponse(
            item.getGameId().getValue().toString(),  // gameId
            gameTitle,                               // title from games table
            coverImageUrl,                           // cover from game_media
            item.getAddedAt(),
            item.getNotificationPreferences(),
            currentPrice
        );
    }
}

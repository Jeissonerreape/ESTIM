package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.presentation.wishlist.WishlistItemResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application service for listing all wishlist items for a user,
 * enriched with game title + cover image + current price from games + game_media.
 */
@Service
public class ListWishlistService {

    private final JdbcTemplate jdbcTemplate;

    public ListWishlistService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WishlistItemResponse> listWishlist(ListWishlistForUserQuery query) {
        UserId userId = query.getUserId();

        String sql = """
            SELECT
              w.game_id   AS game_id,
              w.added_at  AS added_at,
              g.title     AS game_title,
              (
                SELECT gm.url
                FROM game_media gm
                WHERE gm.game_id = g.id
                  AND gm.media_type = 'cover_art'
                ORDER BY gm.display_order ASC
                LIMIT 1
              ) AS cover_image_url,
              g.price     AS current_price
            FROM wishlists w
            JOIN games g ON g.id = w.game_id
            WHERE w.user_id = ?
            ORDER BY w.added_at DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                UUID gameId = UUID.fromString(rs.getString("game_id"));

                Timestamp addedAtTs = rs.getTimestamp("added_at");
                Instant addedAt = addedAtTs != null ? addedAtTs.toInstant() : null;

                String gameTitle = rs.getString("game_title");
                String coverImageUrl = rs.getString("cover_image_url");
                BigDecimal currentPrice = rs.getBigDecimal("current_price");

                // NOTE: schema you pasted for wishlists has no notification_preferences column,
                // so we default to an empty map for now.
                Map<String, Boolean> notificationPreferences = Map.of();

                return new WishlistItemResponse(
                    gameId.toString(),
                    gameTitle,
                    coverImageUrl,
                    addedAt,
                    notificationPreferences,
                    currentPrice
                );
            },
            userId.value()
        );
    }
}

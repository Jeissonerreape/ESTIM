package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.presentation.library.LibraryEntryResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Application service for listing all library entries for a user,
 * enriched with game title + cover image from games + game_media.
 */
@Service
public class ListUserLibraryService {

    private final JdbcTemplate jdbcTemplate;

    public ListUserLibraryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LibraryEntryResponse> listUserLibrary(ListUserLibraryQuery query) {
        UserId userId = query.userId();

        String sql = """
            SELECT
              l.id        AS library_id,
              l.game_id   AS game_id,
              l.source    AS source,
              l.added_at  AS added_at,
              g.title     AS game_title,
              (
                SELECT gm.url
                FROM game_media gm
                WHERE gm.game_id = g.id
                  AND gm.media_type = 'cover_art'
                ORDER BY gm.display_order ASC
                LIMIT 1
              ) AS cover_image_url
            FROM libraries l
            JOIN games g ON g.id = l.game_id
            WHERE l.user_id = ?
            ORDER BY l.added_at DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                UUID libraryId = UUID.fromString(rs.getString("library_id"));
                UUID gameId = UUID.fromString(rs.getString("game_id"));
                String gameTitle = rs.getString("game_title");       // <- from games.title
                String coverImageUrl = rs.getString("cover_image_url"); // <- from game_media.url
                String source = rs.getString("source");
                Timestamp addedAtTs = rs.getTimestamp("added_at");
                Instant addedAt = addedAtTs != null ? addedAtTs.toInstant() : null;

                return new LibraryEntryResponse(
                    libraryId,
                    gameId,
                    gameTitle,
                    coverImageUrl,
                    source,
                    addedAt
                );
            },
            userId.value()
        );
    }
}

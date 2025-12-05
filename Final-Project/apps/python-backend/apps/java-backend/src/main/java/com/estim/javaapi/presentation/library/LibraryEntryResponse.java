package com.estim.javaapi.presentation.library;

import java.time.Instant;
import java.util.UUID;

/**
 * API response representing a game in the user's library.
 *
 * NOTE: Right now this still reflects the libraries table,
 * but we’ve added gameTitle + coverImageUrl for UI convenience.
 */
public record LibraryEntryResponse(
    UUID id,
    UUID gameId,
    String gameTitle,
    String coverImageUrl,
    String source,
    Instant addedAt
) {
}

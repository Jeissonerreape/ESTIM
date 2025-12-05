package com.estim.javaapi.presentation.library;

import java.util.UUID;

/**
 * Request body for adding a game to the current user's library.
 *
 * Example JSON:
 * {
 *   "gameId": "b9e8b7b2-9cfe-4f2a-8b5a-2a4b731c1234",
 *   "source": "PURCHASE"
 * }
 */
public record AddGameToLibraryRequest(
    UUID gameId,
    String source // "PURCHASE" or "GIFT"
) {
}

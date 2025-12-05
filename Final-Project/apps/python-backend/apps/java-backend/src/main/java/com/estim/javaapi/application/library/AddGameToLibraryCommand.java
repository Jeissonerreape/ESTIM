package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.domain.user.UserId;

/**
 * Command to add a game to a user's library.
 */
public record AddGameToLibraryCommand(
    UserId userId,
    GameId gameId,
    LibraryEntrySource source
) {
}

package com.estim.javaapi.domain.library.events;

import com.estim.javaapi.domain.common.AbstractDomainEvent;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Objects;

/**
 * Domain event raised when a game is added to a user's library.
 */
public final class GameAddedToLibrary extends AbstractDomainEvent {

    private final LibraryEntryId libraryEntryId;
    private final UserId userId;
    private final GameId gameId;

    public GameAddedToLibrary(LibraryEntryId libraryEntryId, UserId userId, GameId gameId) {
        this.libraryEntryId = Objects.requireNonNull(libraryEntryId, "libraryEntryId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.gameId = Objects.requireNonNull(gameId, "gameId must not be null");
    }

    public LibraryEntryId getLibraryEntryId() {
        return libraryEntryId;
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }
}

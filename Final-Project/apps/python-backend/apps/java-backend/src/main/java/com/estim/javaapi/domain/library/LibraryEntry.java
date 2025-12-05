package com.estim.javaapi.domain.library;

import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

public class LibraryEntry {

    private final LibraryEntryId id;
    private final UserId userId;
    private final GameId gameId;
    private final LibraryEntrySource source;
    private final Instant addedAt;

    public LibraryEntry(LibraryEntryId id,
                        UserId userId,
                        GameId gameId,
                        LibraryEntrySource source,
                        Instant addedAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.gameId = Objects.requireNonNull(gameId);
        this.source = source; // nullable
        this.addedAt = Objects.requireNonNull(addedAt);
    }

    public static LibraryEntry newEntry(UserId userId,
                                        GameId gameId,
                                        LibraryEntrySource source,
                                        Instant addedAt) {
        return new LibraryEntry(
            LibraryEntryId.randomId(),
            userId,
            gameId,
            source,
            addedAt
        );
    }

    public LibraryEntryId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public GameId getGameId() {
        return gameId;
    }

    public LibraryEntrySource getSource() {
        return source;
    }

    public Instant getAddedAt() {
        return addedAt;
    }
}

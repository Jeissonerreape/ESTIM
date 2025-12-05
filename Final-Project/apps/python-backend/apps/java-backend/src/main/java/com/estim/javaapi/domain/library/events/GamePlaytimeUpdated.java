package com.estim.javaapi.domain.library.events;

import com.estim.javaapi.domain.common.AbstractDomainEvent;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Objects;

/**
 * Domain event raised when playtime for a library entry is updated.
 */
public final class GamePlaytimeUpdated extends AbstractDomainEvent {

    private final LibraryEntryId libraryEntryId;
    private final UserId userId;
    private final long newPlayTimeMinutes;

    public GamePlaytimeUpdated(LibraryEntryId libraryEntryId, UserId userId, long newPlayTimeMinutes) {
        if (newPlayTimeMinutes < 0) {
            throw new IllegalArgumentException("newPlayTimeMinutes must be >= 0");
        }
        this.libraryEntryId = Objects.requireNonNull(libraryEntryId, "libraryEntryId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.newPlayTimeMinutes = newPlayTimeMinutes;
    }

    public LibraryEntryId getLibraryEntryId() {
        return libraryEntryId;
    }

    public UserId getUserId() {
        return userId;
    }

    public long getNewPlayTimeMinutes() {
        return newPlayTimeMinutes;
    }
}

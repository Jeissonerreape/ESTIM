package com.estim.javaapi.domain.library;

import com.estim.javaapi.domain.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing LibraryEntry aggregates.
 */
public interface LibraryRepository {

    Optional<LibraryEntry> findById(LibraryEntryId id);

    Optional<LibraryEntry> findByUserAndGame(UserId userId, GameId gameId);

    List<LibraryEntry> findByUser(UserId userId);

    LibraryEntry save(LibraryEntry entry);
}

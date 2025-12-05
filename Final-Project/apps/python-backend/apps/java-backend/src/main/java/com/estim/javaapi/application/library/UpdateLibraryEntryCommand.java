package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntryStatus;
import com.estim.javaapi.domain.user.UserId;

import java.util.Set;

/**
 * Command to update a library entry.
 *
 * Semantics:
 * - additionalPlayTimeMinutes: minutes to add on top of current playtime (nullable).
 * - tags: if non-null, replaces the entire tag set with the provided one.
 * - status: if non-null, updates the entry status (OWNED / REFUNDED / HIDDEN).
 */
public record UpdateLibraryEntryCommand(
    UserId userId,
    GameId gameId,
    Long additionalPlayTimeMinutes,
    Set<String> tags,
    LibraryEntryStatus status
) {
}

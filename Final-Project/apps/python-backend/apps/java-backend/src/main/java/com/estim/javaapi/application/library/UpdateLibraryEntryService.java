package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.library.LibraryEntry;
import org.springframework.stereotype.Service;

/**
 * Application service for updating an existing library entry.
 *
 * NOTE:
 * The current database schema for `libraries` only supports:
 *   - id, user_id, game_id, source, added_at
 *
 * It does NOT yet support fields like playtime, tags, or status.
 * Therefore, this service is intentionally left unimplemented
 * until the schema is extended.
 */
@Service
public class UpdateLibraryEntryService {

    public LibraryEntry updateLibraryEntry(UpdateLibraryEntryCommand command) {
        throw new UnsupportedOperationException(
            "Updating library entries is not supported by the current schema. " +
                "Extend the `libraries` table before implementing this service."
        );
    }
}

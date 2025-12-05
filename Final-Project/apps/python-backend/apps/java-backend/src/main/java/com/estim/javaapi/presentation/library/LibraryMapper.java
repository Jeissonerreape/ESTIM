package com.estim.javaapi.presentation.library;

import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntrySource;

public final class LibraryMapper {

    private LibraryMapper() {
    }

    /**
     * Basic mapping (no game title / cover).
     * You can keep this for places where you don't need extra info.
     */
    public static LibraryEntryResponse toResponse(LibraryEntry entry) {
        return toResponse(entry, null, null);
    }

    /**
     * Enriched mapping with game title + cover image.
     */
    public static LibraryEntryResponse toResponse(
        LibraryEntry entry,
        String gameTitle,
        String coverImageUrl
    ) {
        LibraryEntrySource source = entry.getSource();

        return new LibraryEntryResponse(
            entry.getId().getValue(),
            entry.getGameId().getValue(),
            gameTitle,                           // may be null
            coverImageUrl,                       // may be null
            source != null ? source.name() : null,
            entry.getAddedAt()
        );
    }
}

package com.estim.javaapi.presentation.library;

import com.estim.javaapi.domain.library.LibraryEntryStatus;

import java.util.Set;

/**
 * Request body for updating a library entry.
 *
 * NOTE:
 * The current schema does not yet store playtime/status/tags, so this
 * is defined for future use. For now, the corresponding service throws
 * UnsupportedOperationException.
 */
public record UpdateLibraryEntryRequest(
    Long additionalPlayTimeMinutes,
    LibraryEntryStatus status,
    Set<String> tags
) {
}

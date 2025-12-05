package com.estim.javaapi.domain.library;

/**
 * Origin of a game in the user's library.
 */
public enum LibraryEntrySource {
    PURCHASE,
    GIFT;

    public static LibraryEntrySource fromDatabaseValue(String value) {
        if (value == null) {
            return null;
        }
        return LibraryEntrySource.valueOf(value.toUpperCase());
    }

    public String toDatabaseValue() {
        return name();
    }
}

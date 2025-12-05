package com.estim.javaapi.domain.library;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for a LibraryEntry aggregate.
 */
public final class LibraryEntryId {

    private final UUID value;

    public LibraryEntryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static LibraryEntryId randomId() {
        return new LibraryEntryId(UUID.randomUUID());
    }

    public static LibraryEntryId of(UUID value) {
        return new LibraryEntryId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibraryEntryId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "LibraryEntryId{" +
            "value=" + value +
            '}';
    }
}

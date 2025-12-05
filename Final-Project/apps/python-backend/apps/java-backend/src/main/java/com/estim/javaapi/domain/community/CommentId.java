package com.estim.javaapi.domain.community;

import java.util.Objects;
import java.util.UUID;

public final class CommentId {

    private final UUID value;

    public CommentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static CommentId newId() {
        return new CommentId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

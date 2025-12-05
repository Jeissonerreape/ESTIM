package com.estim.javaapi.domain.community;

import java.util.Objects;
import java.util.UUID;

public final class CommunityPostId {

    private final UUID value;

    public CommunityPostId(UUID value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public static CommunityPostId newId() {
        return new CommunityPostId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunityPostId that)) return false;
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

package com.estim.javaapi.domain.library;

import java.util.Objects;
import java.util.UUID;

public final class GameId {

    private final UUID value;

    public GameId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static GameId of(UUID value) {
        return new GameId(value);
    }

    public static GameId fromString(String value) {
        return new GameId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "GameId{" +
            "value=" + value +
            '}';
    }
}

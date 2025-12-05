package com.estim.javaapi.domain.user;

import java.util.UUID;

public record PasswordResetTokenId(UUID value) {
    @Override
    public String toString() {
        return value.toString();
    }
}

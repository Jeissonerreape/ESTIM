package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.domain.user.UserId;

/**
 * Represents the authenticated principal in the current request context.
 */
public record AuthenticatedUser(UserId userId) {
}

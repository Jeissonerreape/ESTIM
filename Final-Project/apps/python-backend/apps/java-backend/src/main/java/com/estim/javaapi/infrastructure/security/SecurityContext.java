package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.domain.user.UserId;

import java.util.Optional;

/**
 * Simple thread-local security context used to expose the authenticated user
 * to application and controller layers.
 *
 * In a real HTTP server, a filter/interceptor should:
 *  - set the AuthenticatedUser at the beginning of request handling, and
 *  - clear it at the end.
 */
public final class SecurityContext {

    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    private SecurityContext() {
        // utility class
    }

    public static void setCurrentUser(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static Optional<UserId> getCurrentUserId() {
        return getCurrentUser().map(AuthenticatedUser::userId);
    }

    public static void clear() {
        CURRENT.remove();
    }
}

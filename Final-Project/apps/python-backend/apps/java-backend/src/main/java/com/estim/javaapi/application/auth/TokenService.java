package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Service;

/**
 * Abstraction for issuing and parsing authentication tokens (e.g., JWT).
 */
@Service
public interface TokenService {

    String generateAccessToken(UserId userId);

    String generateRefreshToken(UserId userId);

    /**
     * Extracts the user id from a previously issued access token.
     */
    UserId parseUserIdFromAccessToken(String token);

    /**
     * Revokes a specific refresh token (e.g. blacklist, delete from DB, etc.).
     * Implementations may no-op if revocation is not supported.
     */
    default void revokeRefreshToken(String refreshToken) {
        // default no-op
    }

    /**
     * Revokes all active tokens for a given user, if the implementation supports it.
     */
    default void revokeAllForUser(UserId userId) {
        // default no-op
    }
}

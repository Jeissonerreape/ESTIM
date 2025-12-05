package com.estim.javaapi.domain.user;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository port for PasswordResetToken aggregates.
 */
public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken token);

    /**
     * Find a password reset token by its raw token string
     * (the one sent to the user via email).
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find a password reset token by its identifier.
     * Useful when the domain event carries the token id instead of the raw token.
     */
    Optional<PasswordResetToken> findByTokenId(PasswordResetTokenId tokenId);
}

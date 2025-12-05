package com.estim.javaapi.application.oauth;

/**
 * Input data for logging in using an OAuth provider.
 *
 * This mirrors what the client will know after the OAuth flow:
 * - provider (e.g. "GOOGLE")
 * - external user id (e.g. Google "sub", Steam ID)
 * - email (as reported by provider)
 * - token (access token or auth code)
 */
public record LoginWithOAuthCommand(
    String provider,
    String oauthExternalId,
    String email,
    String token
) {}

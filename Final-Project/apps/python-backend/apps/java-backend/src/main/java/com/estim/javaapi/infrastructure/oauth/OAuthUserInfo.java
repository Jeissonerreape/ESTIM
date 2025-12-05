package com.estim.javaapi.infrastructure.oauth;

/**
 * Simple DTO representing user information obtained from an OAuth provider.
 */
public record OAuthUserInfo(
    String externalUserId,  // provider-specific user id ("sub", SteamID, etc.)
    String email,
    String displayName
) {}

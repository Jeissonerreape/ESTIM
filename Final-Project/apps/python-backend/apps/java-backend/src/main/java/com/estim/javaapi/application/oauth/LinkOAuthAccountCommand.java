package com.estim.javaapi.application.oauth;

import org.springframework.stereotype.Component;

/**
 * Input data for linking an OAuth account to an existing user.
 *
 * Typically the frontend will get oauthExternalId + email from the OAuth provider
 * (e.g. Google "sub" and email) after completing the OAuth flow in the browser.
 */
public record LinkOAuthAccountCommand(
    String userId,        // internal user id (UUID string)
    String provider,      // "google", "steam", etc.
    String externalToken, // OAuth access token or authorization code
    String redirectUri    // optional
) {}

package com.estim.javaapi.presentation.auth;

public record OAuthLinkRequest(
    String provider,      // "google", "steam", etc.
    String externalToken, // the OAuth access token or authorization code
    String redirectUri    // optional, depends on your OAuth flow
) {}

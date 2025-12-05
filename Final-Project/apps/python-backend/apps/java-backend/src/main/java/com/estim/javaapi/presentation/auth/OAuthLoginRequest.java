package com.estim.javaapi.presentation.auth;

public record OAuthLoginRequest(
    String provider,
    String externalToken,
    String redirectUri
) {}

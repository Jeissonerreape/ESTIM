package com.estim.javaapi.infrastructure.oauth;

import java.util.Objects;

/**
 * Minimal skeleton client for Steam OAuth / OpenID integration.
 *
 * The exact implementation depends on how you integrate with Steam:
 *  - OpenID return URL validation (older flow)
 *  - Steam Web API + tokens
 *
 * For now, this class acts as a placeholder that you can extend later.
 */
public class SteamOAuthClient {

    private final String apiBaseUrl;
    private final String apiKey;

    /**
     * @param apiBaseUrl Steam Web API base URL, e.g. "https://api.steampowered.com"
     * @param apiKey     Steam Web API key (if needed for certain endpoints)
     */
    public SteamOAuthClient(String apiBaseUrl, String apiKey) {
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl);
        this.apiKey = Objects.requireNonNull(apiKey);
    }

    /**
     * Fetches user information from Steam given some Steam/OpenID identifier or token.
     *
     * How you obtain this identifier depends on your Steam auth flow.
     * This method is intentionally a stub that you can adapt later.
     *
     * @param steamIdentifier SteamID, OpenID claimed_id, or other identifier
     * @return OAuthUserInfo with externalUserId, email (if available), and displayName
     */
    public OAuthUserInfo fetchUserInfo(String steamIdentifier) {
        Objects.requireNonNull(steamIdentifier, "steamIdentifier must not be null");

        // TODO: Implement actual Steam Web API calls here.
        // For now, this is just a stub to keep the project compiling and structured.

        String externalUserId = steamIdentifier;
        String email = null; // Steam typically doesn't give you email directly.
        String displayName = "Steam User";

        return new OAuthUserInfo(externalUserId, email, displayName);
    }
}

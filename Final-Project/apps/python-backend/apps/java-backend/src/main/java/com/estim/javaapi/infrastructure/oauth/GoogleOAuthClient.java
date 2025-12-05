package com.estim.javaapi.infrastructure.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Minimal integration client for Google OAuth.
 *
 * Given a Google OAuth access token, this client can fetch basic user info
 * from the Google UserInfo endpoint.
 *
 * NOTE: This is a skeleton implementation. You should:
 *  - replace naive response handling with proper JSON parsing (e.g. Jackson),
 *  - add error handling / logging as needed.
 */
public class GoogleOAuthClient {

    // Default Google userinfo endpoint
    private static final String DEFAULT_USERINFO_ENDPOINT =
        "https://www.googleapis.com/oauth2/v3/userinfo";

    private final String userInfoEndpoint;

    public GoogleOAuthClient() {
        this(DEFAULT_USERINFO_ENDPOINT);
    }

    public GoogleOAuthClient(String userInfoEndpoint) {
        this.userInfoEndpoint = Objects.requireNonNull(userInfoEndpoint);
    }

    /**
     * Fetches user information from Google given a valid OAuth access token.
     *
     * @param accessToken Google OAuth access token (bearer token)
     * @return OAuthUserInfo with externalUserId, email, and displayName
     */
    public OAuthUserInfo fetchUserInfo(String accessToken) {
        Objects.requireNonNull(accessToken, "accessToken must not be null");

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(userInfoEndpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Google userinfo call failed with HTTP status: " + status);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();

            // TODO: parse JSON properly using Jackson or similar:
            // {
            //   "sub": "110169484474386276334",
            //   "name": "John Doe",
            //   "given_name": "...",
            //   "family_name": "...",
            //   "picture": "...",
            //   "email": "john.doe@example.com",
            //   ...
            // }

            // For now, this is a stub that just returns a generic user.
            // Replace with real JSON parsing later.
            String externalUserId = "google-" + accessToken.hashCode();
            String email = "unknown@example.com";
            String displayName = "Google User";

            return new OAuthUserInfo(externalUserId, email, displayName);

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch Google user info", e);
        }
    }
}

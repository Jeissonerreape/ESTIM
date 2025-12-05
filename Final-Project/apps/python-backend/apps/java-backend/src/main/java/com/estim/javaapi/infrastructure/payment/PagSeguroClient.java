package com.estim.javaapi.infrastructure.payment;

import com.estim.javaapi.application.payment.PaymentProviderClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Integration client for PagSeguro (Brazilian payment provider).
 *
 * This class represents the infrastructure boundary for validating payment tokens
 * or vaulted card references returned by PagSeguro.
 *
 * You may later extend this class to:
 *   - create vaulted payment methods
 *   - create transactions / pre-authorizations
 *   - capture or refund payments
 */
public class PagSeguroClient implements PaymentProviderClient {

    private final String apiBaseUrl;
    private final String authToken;

    /**
     * @param apiBaseUrl e.g. "https://api.pagseguro.com"
     * @param authToken  PagSeguro personal or application token
     */
    public PagSeguroClient(String apiBaseUrl, String authToken) {
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl);
        this.authToken = Objects.requireNonNull(authToken);
    }

    /**
     * Validates a token returned by PagSeguro's JS SDK or backend SDK.
     * For example, "card token" or "vault ID".
     *
     * This method performs a minimal validation by calling PagSeguro
     * to check whether the token exists and is authorized for your account.
     *
     * If the token is invalid or cannot be verified, this method throws an exception.
     */
    @Override
    public void validateToken(String provider, String externalToken) {
        if (!"PAGSEGURO".equalsIgnoreCase(provider)) {
            throw new IllegalArgumentException("Provider not supported: " + provider);
        }

        Objects.requireNonNull(externalToken, "externalToken must not be null");

        // You can change this endpoint depending on which PagSeguro API you’re using.
        String validateUrl = apiBaseUrl + "/v1/tokens/validate";

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(validateUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + authToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Minimal JSON body: adjust to real PagSeguro token validation structure
            String jsonBody = """
                    {
                      "token": "%s"
                    }
                    """.formatted(externalToken);

            conn.getOutputStream().write(jsonBody.getBytes(StandardCharsets.UTF_8));

            int status = conn.getResponseCode();
            if (status != 200 && status != 201) {
                throw new RuntimeException(
                    "PagSeguro token validation failed with HTTP status: " + status
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to validate PagSeguro token", e);
        }
    }
}

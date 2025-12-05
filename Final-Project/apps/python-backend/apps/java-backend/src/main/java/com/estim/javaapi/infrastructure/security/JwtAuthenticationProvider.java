package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.user.UserId;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JwtAuthenticationProvider {

    private final TokenService tokenService;

    public JwtAuthenticationProvider(TokenService tokenService) {
        this.tokenService = Objects.requireNonNull(tokenService);
    }

    public void authenticateFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        }

        String token = authorizationHeader.substring(prefix.length()).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Empty Bearer token");
        }

        try {
            UserId userId = tokenService.parseUserIdFromAccessToken(token);
            AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId);
            SecurityContext.setCurrentUser(authenticatedUser);

        } catch (ExpiredJwtException ex) {
            // Token is structurally valid but no longer valid in time
            throw new IllegalArgumentException("Access token expired", ex);

        } catch (JwtException | IllegalArgumentException ex) {
            // Any other parsing/validation error
            throw new IllegalArgumentException("Invalid or malformed access token", ex);
        }
    }

    public void clearAuthentication() {
        SecurityContext.clear();
    }
}

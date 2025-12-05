package com.estim.javaapi.application.oauth;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.auth.TokenService;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.*;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

/**
 * Application service for logging in a user using an OAuth provider.
 *
 * Now we:
 *  - look up the user by (provider, externalUserId),
 *  - generate tokens for that user.
 *
 * The linking is handled separately by LinkOAuthAccountService.
 */
@Component
public class LoginWithOAuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;

    public LoginWithOAuthService(UserRepository userRepository,
                                 TokenService tokenService,
                                 DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public AuthenticationResult login(LoginWithOAuthCommand command) {
        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());
        String externalId = command.oauthExternalId();

        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("Missing OAuth external id");
        }

        User user = userRepository
            .findByOAuthProviderAndExternalId(provider, externalId)
            .orElseThrow(() -> new IllegalArgumentException("User not found for OAuth login"));

        user.markLogin();
        userRepository.save(user);

        UserId userId = user.id();
        String accessToken = tokenService.generateAccessToken(userId);
        String refreshToken = tokenService.generateRefreshToken(userId);

        eventPublisher.publish(new UserLoggedIn(userId, Instant.now()));

        return new AuthenticationResult(accessToken, refreshToken, user);
    }
}

package com.estim.javaapi.application.oauth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.OAuthAccount;
import com.estim.javaapi.domain.user.OAuthAccountId;
import com.estim.javaapi.domain.user.OAuthProvider;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service for linking an OAuth account to an existing user.
 *
 * NOTE: For now, we treat externalToken as the external identifier.
 * Later you can integrate GoogleOAuthClient / SteamOAuthClient to
 * exchange the token for real user info (externalUserId + email).
 */
@Component
public class LinkOAuthAccountService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public LinkOAuthAccountService(UserRepository userRepository,
                                   DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void link(LinkOAuthAccountCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        OAuthProvider provider = OAuthProvider.valueOf(command.provider().toUpperCase());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // TODO: Use provider + externalToken (+ redirectUri) to call Google/Steam
        // and obtain real externalUserId + email. For now, we just reuse the token.
        String externalUserId = command.externalToken();
        String email = null;

        OAuthAccount account = new OAuthAccount(
            new OAuthAccountId(UUID.randomUUID()),
            provider,
            externalUserId,
            email,
            Instant.now()
        );

        user.linkOAuthAccount(account);

        userRepository.save(user);

        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}

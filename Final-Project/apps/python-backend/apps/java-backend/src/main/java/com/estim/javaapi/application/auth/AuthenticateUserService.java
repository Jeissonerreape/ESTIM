package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserRepository;
import com.estim.javaapi.domain.user.events.UserLoggedIn;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class AuthenticateUserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final DomainEventPublisher eventPublisher;

    public AuthenticateUserService(UserRepository userRepository,
                                   PasswordHasher passwordHasher,
                                   TokenService tokenService,
                                   DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.tokenService = Objects.requireNonNull(tokenService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public AuthenticationResult authenticate(AuthenticateUserCommand command) {
        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordHasher.matches(command.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.status().canLogin()) {
            throw new IllegalStateException("User is not allowed to log in");
        }

        String accessToken = tokenService.generateAccessToken(user.id());
        String refreshToken = tokenService.generateRefreshToken(user.id());

        eventPublisher.publish(new UserLoggedIn(user.id(), Instant.now()));

        return new AuthenticationResult(accessToken, refreshToken, user);
    }
}

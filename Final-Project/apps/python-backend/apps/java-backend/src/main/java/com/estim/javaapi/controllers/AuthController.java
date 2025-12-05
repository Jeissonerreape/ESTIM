package com.estim.javaapi.controllers;

import com.estim.javaapi.application.auth.*;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.auth.LoginRequest;
import com.estim.javaapi.presentation.auth.LoginResponse;
import com.estim.javaapi.presentation.auth.RegisterUserRequest;
import com.estim.javaapi.presentation.auth.RegisterUserResponse;
import com.estim.javaapi.presentation.auth.CurrentUserResponse;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.common.UserDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserService registerUserService;
    private final AuthenticateUserService authenticateUserService;
    private final LogoutUserService logoutUserService;
    private final GetCurrentUserService getCurrentUserService;
    private final JwtAuthenticationProvider authenticationProvider;

    public AuthController(RegisterUserService registerUserService,
                          AuthenticateUserService authenticateUserService,
                          LogoutUserService logoutUserService,
                          GetCurrentUserService getCurrentUserService,
                          JwtAuthenticationProvider authenticationProvider) {

        this.registerUserService = registerUserService;
        this.authenticateUserService = authenticateUserService;
        this.logoutUserService = logoutUserService;
        this.getCurrentUserService = getCurrentUserService;
        this.authenticationProvider = authenticationProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        try {
            RegisterUserCommand command = new RegisterUserCommand(
                request.email(),
                request.password(),
                request.displayName()
            );

            User user = registerUserService.register(command);
            RegisterUserResponse response = UserDtoMapper.toRegisterUserResponse(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthenticateUserCommand command = new AuthenticateUserCommand(
                request.email(),
                request.password()
            );

            AuthenticationResult result = authenticateUserService.authenticate(command);
            LoginResponse response = UserDtoMapper.toLoginResponse(result);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("AUTH_FAILED", ex.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        String accessToken = extractBearerTokenOrNull(authorizationHeader);
        logoutUserService.logout(new LogoutUserCommand(accessToken, null));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
        @org.springframework.security.core.annotation.AuthenticationPrincipal
        com.estim.javaapi.infrastructure.security.AuthenticatedUser currentUser
    ) {
        if (currentUser == null) {
            // No or invalid JWT → treat as AUTH_FAILED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("AUTH_FAILED", "No authenticated user", null));
        }

        var userId = currentUser.userId();
        User user = getCurrentUserService.getCurrentUser(userId);
        CurrentUserResponse response = UserDtoMapper.toCurrentUserResponse(user);
        return ResponseEntity.ok(response);
    }

    // ---------- Helpers ----------

    private String extractBearerTokenOrNull(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            return null;
        }
        String token = authorizationHeader.substring(prefix.length()).trim();
        return token.isEmpty() ? null : token;
    }
}

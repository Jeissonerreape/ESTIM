package com.estim.javaapi.controllers;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.oauth.LinkOAuthAccountCommand;
import com.estim.javaapi.application.oauth.LinkOAuthAccountService;
import com.estim.javaapi.application.oauth.LoginWithOAuthCommand;
import com.estim.javaapi.application.oauth.LoginWithOAuthService;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.auth.LoginResponse;
import com.estim.javaapi.presentation.auth.OAuthLinkRequest;
import com.estim.javaapi.presentation.auth.OAuthLoginRequest;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.common.UserDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/oauth")
public class OAuthController {

    private final LinkOAuthAccountService linkOAuthAccountService;
    private final LoginWithOAuthService loginWithOAuthService;
    private final JwtAuthenticationProvider authenticationProvider;

    public OAuthController(LinkOAuthAccountService linkOAuthAccountService,
                           LoginWithOAuthService loginWithOAuthService,
                           JwtAuthenticationProvider authenticationProvider,
                           UserDtoMapper userDtoMapper) {

        this.linkOAuthAccountService = linkOAuthAccountService;
        this.loginWithOAuthService = loginWithOAuthService;
        this.authenticationProvider = authenticationProvider;
    }

    @PostMapping("/link")
    public ResponseEntity<?> link(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestBody OAuthLinkRequest request) {

        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            var userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No authenticated user"));

            LinkOAuthAccountCommand command = new LinkOAuthAccountCommand(
                userId.value().toString(),
                request.provider(),
                request.externalToken(),
                request.redirectUri()
            );

            linkOAuthAccountService.link(command);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("OAUTH_LINK_FAILED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginWithOAuth(@RequestBody OAuthLoginRequest request) {
        try {
            LoginWithOAuthCommand command = new LoginWithOAuthCommand(
                request.provider(),
                request.externalToken(), // used as oauthExternalId for now
                null,                    // email unknown at this stage
                request.externalToken()  // also used as "token" for now
            );

            AuthenticationResult result = loginWithOAuthService.login(command);
            LoginResponse response = UserDtoMapper.toLoginResponse(result);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("OAUTH_LOGIN_FAILED", ex.getMessage(), null));
        }
    }
}

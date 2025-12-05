package com.estim.javaapi.controllers;

import com.estim.javaapi.application.auth.AuthenticationResult;
import com.estim.javaapi.application.oauth.LinkOAuthAccountCommand;
import com.estim.javaapi.application.oauth.LinkOAuthAccountService;
import com.estim.javaapi.application.oauth.LoginWithOAuthCommand;
import com.estim.javaapi.application.oauth.LoginWithOAuthService;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.auth.OAuthLinkRequest;
import com.estim.javaapi.presentation.auth.OAuthLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LinkOAuthAccountService linkOAuthAccountService;

    @MockitoBean
    private LoginWithOAuthService loginWithOAuthService;

    @MockitoBean
    private JwtAuthenticationProvider authenticationProvider;

    // ---------- Helper fixtures ----------

    private User buildTestUser() {
        User user = mock(User.class);
        UserId userId = mock(UserId.class);
        Email email = mock(Email.class);
        UserProfile profile = mock(UserProfile.class);

        when(user.id()).thenReturn(userId);
        when(userId.toString()).thenReturn("user-123");

        when(user.email()).thenReturn(email);
        when(email.value()).thenReturn("john@example.com");

        when(user.profile()).thenReturn(profile);
        when(profile.displayName()).thenReturn("John Doe");
        when(user.emailVerified()).thenReturn(true);

        return user;
    }

    // ======================================================
    //               POST /auth/oauth/link
    // ======================================================

    @Nested
    @DisplayName("POST /auth/oauth/link")
    class LinkOAuthAccountTests {

        @Test
        @DisplayName("should link OAuth account for authenticated user and return 204")
        void linkSuccess() throws Exception {
            String header = "Bearer valid-token";
            String provider = "STEAM";
            String externalToken = "steam-external-token";
            String redirectUri = "https://app.example.com/oauth/callback";

            OAuthLinkRequest request = new OAuthLinkRequest(
                provider,
                externalToken,
                redirectUri
            );

            // auth ok
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            // current user id
            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(currentUserId.value()).thenReturn(userUuid);

            doNothing().when(linkOAuthAccountService)
                .link(any(LinkOAuthAccountCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(post("/auth/oauth/link")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

                ArgumentCaptor<LinkOAuthAccountCommand> captor =
                    ArgumentCaptor.forClass(LinkOAuthAccountCommand.class);

                verify(linkOAuthAccountService).link(captor.capture());
                LinkOAuthAccountCommand cmd = captor.getValue();

                assertThat(cmd.userId()).isEqualTo(userUuid.toString());
                assertThat(cmd.provider()).isEqualTo(provider);
                assertThat(cmd.externalToken()).isEqualTo(externalToken);
                assertThat(cmd.redirectUri()).isEqualTo(redirectUri);
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 400 OAUTH_LINK_FAILED when auth fails")
        void linkAuthFails() throws Exception {
            OAuthLinkRequest request = new OAuthLinkRequest(
                "STEAM",
                "token",
                "https://app.example.com/oauth/callback"
            );

            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(post("/auth/oauth/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("OAUTH_LINK_FAILED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(linkOAuthAccountService);
        }

        @Test
        @DisplayName("should return 400 OAUTH_LINK_FAILED when SecurityContext has no current user")
        void linkNoCurrentUser() throws Exception {
            String header = "Bearer valid-token";

            OAuthLinkRequest request = new OAuthLinkRequest(
                "STEAM",
                "token",
                "https://app.example.com/oauth/callback"
            );

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(post("/auth/oauth/link")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("OAUTH_LINK_FAILED"))
                    .andExpect(jsonPath("$.message").value("No authenticated user"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(linkOAuthAccountService);
        }

        @Test
        @DisplayName("should return 400 OAUTH_LINK_FAILED when link service throws IllegalArgumentException")
        void linkServiceValidationError() throws Exception {
            String header = "Bearer valid-token";

            OAuthLinkRequest request = new OAuthLinkRequest(
                "UNKNOWN_PROVIDER",
                "token",
                "https://app.example.com/oauth/callback"
            );

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
            when(currentUserId.value()).thenReturn(userUuid);

            doThrow(new IllegalArgumentException("Unsupported provider"))
                .when(linkOAuthAccountService)
                .link(any(LinkOAuthAccountCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(post("/auth/oauth/link")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("OAUTH_LINK_FAILED"))
                    .andExpect(jsonPath("$.message").value("Unsupported provider"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }
    }

    // ======================================================
    //               POST /auth/oauth/login
    // ======================================================

    @Nested
    @DisplayName("POST /auth/oauth/login")
    class OAuthLoginTests {

        @Test
        @DisplayName("should login with OAuth provider and return LoginResponse")
        void oauthLoginSuccess() throws Exception {
            String provider = "STEAM";
            String externalToken = "steam-token-xyz";
            String redirectUri = "https://app.example.com/oauth/callback";

            OAuthLoginRequest request = new OAuthLoginRequest(
                provider,
                externalToken,
                redirectUri
            );

            User user = buildTestUser();
            AuthenticationResult authResult = mock(AuthenticationResult.class);
            when(authResult.user()).thenReturn(user);
            when(authResult.accessToken()).thenReturn("access-token-123");
            when(authResult.refreshToken()).thenReturn("refresh-token-456");

            when(loginWithOAuthService.login(any(LoginWithOAuthCommand.class)))
                .thenReturn(authResult);

            mockMvc.perform(post("/auth/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userId").value("user-123"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"))
                .andExpect(jsonPath("$.user.displayName").value("John Doe"))
                .andExpect(jsonPath("$.user.emailVerified").value(true));

            verify(loginWithOAuthService).login(any(LoginWithOAuthCommand.class));
        }

        @Test
        @DisplayName("should return 401 OAUTH_LOGIN_FAILED when service throws IllegalArgumentException")
        void oauthLoginFailsIllegalArgument() throws Exception {
            OAuthLoginRequest request = new OAuthLoginRequest(
                "STEAM",
                "invalid-token",
                "https://app.example.com/oauth/callback"
            );

            when(loginWithOAuthService.login(any(LoginWithOAuthCommand.class)))
                .thenThrow(new IllegalArgumentException("Invalid OAuth token"));

            mockMvc.perform(post("/auth/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("OAUTH_LOGIN_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid OAuth token"));

            verify(loginWithOAuthService).login(any(LoginWithOAuthCommand.class));
        }

        @Test
        @DisplayName("should return 401 OAUTH_LOGIN_FAILED when service throws IllegalStateException")
        void oauthLoginFailsIllegalState() throws Exception {
            OAuthLoginRequest request = new OAuthLoginRequest(
                "STEAM",
                "valid-but-unlinked-token",
                "https://app.example.com/oauth/callback"
            );

            when(loginWithOAuthService.login(any(LoginWithOAuthCommand.class)))
                .thenThrow(new IllegalStateException("OAuth account not linked"));

            mockMvc.perform(post("/auth/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("OAUTH_LOGIN_FAILED"))
                .andExpect(jsonPath("$.message").value("OAuth account not linked"));

            verify(loginWithOAuthService).login(any(LoginWithOAuthCommand.class));
        }
    }
}

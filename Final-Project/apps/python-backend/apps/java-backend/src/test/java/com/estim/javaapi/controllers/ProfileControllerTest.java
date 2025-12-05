package com.estim.javaapi.controllers;

import com.estim.javaapi.application.profile.GetUserProfileQuery;
import com.estim.javaapi.application.profile.GetUserProfileService;
import com.estim.javaapi.application.profile.UpdateUserProfileCommand;
import com.estim.javaapi.application.profile.UpdateUserProfileService;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.PrivacySettings;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.profile.UpdateUserProfileRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetUserProfileService getUserProfileService;

    @MockitoBean
    private UpdateUserProfileService updateUserProfileService;

    @MockitoBean
    private JwtAuthenticationProvider authenticationProvider;

    // ---------- Helper fixtures ----------

    /**
     * Build a fully stubbed User with profile & privacy so UserDtoMapper works.
     */
    private User buildTestUserWithProfile() {
        User user = mock(User.class);
        UserId userId = mock(UserId.class);
        Email email = mock(Email.class);
        UserProfile profile = mock(UserProfile.class);
        PrivacySettings privacy = mock(PrivacySettings.class);

        // id
        when(user.id()).thenReturn(userId);
        when(userId.toString()).thenReturn("user-123");

        // email
        when(user.email()).thenReturn(email);
        when(email.value()).thenReturn("john@example.com");

        // profile
        when(user.profile()).thenReturn(profile);
        when(profile.displayName()).thenReturn("John Doe");
        when(profile.avatarUrl()).thenReturn("https://example.com/avatar.png");
        when(profile.bio()).thenReturn("Hello, I am John");
        when(profile.location()).thenReturn("Bogotá");
        when(profile.privacySettings()).thenReturn(privacy);

        // privacy
        when(privacy.showProfile()).thenReturn(true);
        when(privacy.showActivity()).thenReturn(false);
        when(privacy.showWishlist()).thenReturn(true);

        return user;
    }

    // ======================================================
    //              GET /users/{id}/profile
    // ======================================================

    @Nested
    @DisplayName("GET /users/{id}/profile")
    class GetProfileTests {

        @Test
        @DisplayName("should return public profile when authenticated viewer requests it")
        void getProfileAsAuthenticatedViewer() throws Exception {
            String targetUserId = "target-user-id-123";
            String header = "Bearer valid-token";

            // authenticate successfully
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            // current authenticated user id
            UserId currentUserId = mock(UserId.class);
            UUID requesterUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
            when(currentUserId.value()).thenReturn(requesterUuid);

            User user = buildTestUserWithProfile();
            when(getUserProfileService.getProfile(any(GetUserProfileQuery.class)))
                .thenReturn(user);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                // When / Then
                mockMvc.perform(get("/users/{id}/profile", targetUserId)
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    // UserProfileResponse(userId, displayName, avatarUrl, bio, location, privacy)
                    .andExpect(jsonPath("$.userId").value("user-123"))
                    .andExpect(jsonPath("$.displayName").value("John Doe"))
                    .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.png"))
                    .andExpect(jsonPath("$.bio").value("Hello, I am John"))
                    .andExpect(jsonPath("$.location").value("Bogotá"))
                    .andExpect(jsonPath("$.privacy.showProfile").value(true))
                    .andExpect(jsonPath("$.privacy.showActivity").value(false))
                    .andExpect(jsonPath("$.privacy.showWishlist").value(true));

                // Capture and verify query
                ArgumentCaptor<GetUserProfileQuery> queryCaptor =
                    ArgumentCaptor.forClass(GetUserProfileQuery.class);

                verify(getUserProfileService).getProfile(queryCaptor.capture());
                GetUserProfileQuery query = queryCaptor.getValue();
                assertThat(query.requesterUserId())
                    .isEqualTo(requesterUuid.toString());
                assertThat(query.targetUserId())
                    .isEqualTo(targetUserId);

                verify(authenticationProvider)
                    .authenticateFromAuthorizationHeader(header);
            }

            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return public profile to anonymous viewer (auth fails, requesterId = all-zeros)")
        void getProfileAsAnonymousViewer() throws Exception {
            String targetUserId = "target-user-id-123";

            // authentication throws (e.g. missing/invalid token)
            doThrow(new IllegalArgumentException("Invalid token"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            User user = buildTestUserWithProfile();
            when(getUserProfileService.getProfile(any(GetUserProfileQuery.class)))
                .thenReturn(user);

            // When / Then
            mockMvc.perform(get("/users/{id}/profile", targetUserId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("user-123"));

            // Capture query to assert default requesterId
            ArgumentCaptor<GetUserProfileQuery> queryCaptor =
                ArgumentCaptor.forClass(GetUserProfileQuery.class);

            verify(getUserProfileService).getProfile(queryCaptor.capture());
            GetUserProfileQuery query = queryCaptor.getValue();
            assertThat(query.requesterUserId())
                .isEqualTo("00000000-0000-0000-0000-000000000000");
            assertThat(query.targetUserId())
                .isEqualTo(targetUserId);

            // clearAuthentication should still be called
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 403 PROFILE_PRIVATE when profile is private for this viewer")
        void getProfilePrivate() throws Exception {
            String targetUserId = "target-user-id-123";

            // auth may succeed or not; not relevant here
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            when(getUserProfileService.getProfile(any(GetUserProfileQuery.class)))
                .thenThrow(new IllegalStateException("Profile is private"));

            mockMvc.perform(get("/users/{id}/profile", targetUserId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PROFILE_PRIVATE"))
                .andExpect(jsonPath("$.message").value("Profile is private"));

            verify(getUserProfileService).getProfile(any(GetUserProfileQuery.class));
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 404 PROFILE_NOT_FOUND when user profile does not exist")
        void getProfileNotFound() throws Exception {
            String targetUserId = "non-existent";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            when(getUserProfileService.getProfile(any(GetUserProfileQuery.class)))
                .thenThrow(new IllegalArgumentException("Profile not found"));

            mockMvc.perform(get("/users/{id}/profile", targetUserId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PROFILE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Profile not found"));

            verify(getUserProfileService).getProfile(any(GetUserProfileQuery.class));
            verify(authenticationProvider).clearAuthentication();
        }
    }

    // ======================================================
    //                   PUT /me/profile
    // ======================================================

    @Nested
    @DisplayName("PUT /me/profile")
    class UpdateProfileTests {

        @Test
        @DisplayName("should update profile for authenticated user and return 204")
        void updateProfileSuccess() throws Exception {
            String header = "Bearer valid-token";

            // auth ok
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            // current user id via SecurityContext
            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("22222222-2222-2222-2222-222222222222");
            when(currentUserId.value()).thenReturn(userUuid);

            UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                "New Name",
                "https://example.com/new-avatar.png",
                "New bio",
                "Medellín",
                true,
                true,
                false
            );

            doNothing().when(updateUserProfileService)
                .updateProfile(any(UpdateUserProfileCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(put("/me/profile")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());

                // Capture command to assert fields
                ArgumentCaptor<UpdateUserProfileCommand> cmdCaptor =
                    ArgumentCaptor.forClass(UpdateUserProfileCommand.class);

                verify(updateUserProfileService)
                    .updateProfile(cmdCaptor.capture());
                UpdateUserProfileCommand cmd = cmdCaptor.getValue();

                assertThat(cmd.userId()).isEqualTo(userUuid.toString());
                assertThat(cmd.displayName()).isEqualTo("New Name");
                assertThat(cmd.avatarUrl()).isEqualTo("https://example.com/new-avatar.png");
                assertThat(cmd.bio()).isEqualTo("New bio");
                assertThat(cmd.location()).isEqualTo("Medellín");
                assertThat(cmd.showProfile()).isTrue();
                assertThat(cmd.showActivity()).isTrue();
                assertThat(cmd.showWishlist()).isFalse();

                verify(authenticationProvider)
                    .authenticateFromAuthorizationHeader(header);
            }

            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 400 PROFILE_UPDATE_FAILED when not authenticated")
        void updateProfileNotAuthenticated() throws Exception {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                "Name",
                "https://example.com/avatar.png",
                "Bio",
                "Location",
                true,
                true,
                true
            );

            // authenticateFromAuthorizationHeader will fail (e.g. missing header -> null)
            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(put("/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PROFILE_UPDATE_FAILED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(updateUserProfileService);
        }

        @Test
        @DisplayName("should return 400 PROFILE_UPDATE_FAILED when update service throws IllegalArgumentException")
        void updateProfileServiceValidationError() throws Exception {
            String header = "Bearer valid-token";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("33333333-3333-3333-3333-333333333333");
            when(currentUserId.value()).thenReturn(userUuid);

            UpdateUserProfileRequest request = new UpdateUserProfileRequest(
                "", // invalid display name
                null,
                "Bio",
                "Location",
                true,
                false,
                true
            );

            doThrow(new IllegalArgumentException("Invalid display name"))
                .when(updateUserProfileService)
                .updateProfile(any(UpdateUserProfileCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(put("/me/profile")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PROFILE_UPDATE_FAILED"))
                    .andExpect(jsonPath("$.message").value("Invalid display name"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }
    }
}

package com.estim.javaapi.controllers;

import com.estim.javaapi.application.profile.GetUserProfileQuery;
import com.estim.javaapi.application.profile.GetUserProfileService;
import com.estim.javaapi.application.profile.UpdateUserProfileCommand;
import com.estim.javaapi.application.profile.UpdateUserProfileService;
import com.estim.javaapi.presentation.profile.UpdateUserProfileRequest;
import com.estim.javaapi.presentation.profile.UserProfileResponse;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.common.UserDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProfileController {

    private final GetUserProfileService getUserProfileService;
    private final UpdateUserProfileService updateUserProfileService;
    private final JwtAuthenticationProvider authenticationProvider;

    public ProfileController(GetUserProfileService getUserProfileService,
                             UpdateUserProfileService updateUserProfileService,
                             JwtAuthenticationProvider authenticationProvider) {

        this.getUserProfileService = getUserProfileService;
        this.updateUserProfileService = updateUserProfileService;
        this.authenticationProvider = authenticationProvider;
    }

    @GetMapping("/users/{id}/profile")
    public ResponseEntity<?> getProfile(
        @PathVariable("id") String targetUserId,
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        try {
            // Requester is optional (anonymous) – if no auth, we pass some dummy id
            String requesterId = "00000000-0000-0000-0000-000000000000";
            try {
                authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);
                requesterId = SecurityContext.getCurrentUserId()
                    .map(id -> id.value().toString())
                    .orElse(requesterId);
            } catch (Exception ignored) {
                // anonymous viewer
            }

            GetUserProfileQuery query = new GetUserProfileQuery(requesterId, targetUserId);
            User user = getUserProfileService.getProfile(query);
            UserProfileResponse response = UserDtoMapper.toUserProfileResponse(user);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("PROFILE_PRIVATE", ex.getMessage(), null));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("PROFILE_NOT_FOUND", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateProfile(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestBody UpdateUserProfileRequest request) {

        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            var userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                userId.value().toString(),
                request.displayName(),
                request.avatarUrl(),
                request.bio(),
                request.location(),
                request.showProfile(),
                request.showActivity(),
                request.showWishlist()
            );

            updateUserProfileService.updateProfile(command);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("PROFILE_UPDATE_FAILED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }
}

package com.estim.javaapi.controllers;

import com.estim.javaapi.application.password.RequestPasswordResetCommand;
import com.estim.javaapi.application.password.RequestPasswordResetService;
import com.estim.javaapi.application.password.ResetPasswordCommand;
import com.estim.javaapi.application.password.ResetPasswordService;
import com.estim.javaapi.presentation.password.PasswordResetPerformRequest;
import com.estim.javaapi.presentation.password.PasswordResetRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestPasswordResetService requestPasswordResetService;

    @MockitoBean
    private ResetPasswordService resetPasswordService;

    // ======================================================
    //        POST /auth/password/reset-request
    // ======================================================

    @Nested
    @DisplayName("POST /auth/password/reset-request")
    class RequestResetTests {

        @Test
        @DisplayName("should request password reset and return generic success response")
        void requestResetSuccess() throws Exception {
            String email = "john@example.com";

            PasswordResetRequest request = new PasswordResetRequest(email);

            // service does nothing (success)
            doNothing().when(requestPasswordResetService)
                .requestReset(any(RequestPasswordResetCommand.class));

            mockMvc.perform(post("/auth/password/reset-request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // PasswordResetResponse(success, message)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(
                    "If an account exists for that email, a reset link has been sent."
                ));

            // capture command
            ArgumentCaptor<RequestPasswordResetCommand> captor =
                ArgumentCaptor.forClass(RequestPasswordResetCommand.class);

            verify(requestPasswordResetService).requestReset(captor.capture());
            RequestPasswordResetCommand cmd = captor.getValue();
            assertThat(cmd.email()).isEqualTo(email);
        }

        @Test
        @DisplayName("should return 400 RESET_REQUEST_FAILED when service throws IllegalArgumentException")
        void requestResetValidationError() throws Exception {
            PasswordResetRequest request = new PasswordResetRequest("not-an-email");

            doThrow(new IllegalArgumentException("Invalid email"))
                .when(requestPasswordResetService)
                .requestReset(any(RequestPasswordResetCommand.class));

            mockMvc.perform(post("/auth/password/reset-request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("RESET_REQUEST_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid email"));

            verify(requestPasswordResetService)
                .requestReset(any(RequestPasswordResetCommand.class));
        }
    }

    // ======================================================
    //            POST /auth/password/reset
    // ======================================================

    @Nested
    @DisplayName("POST /auth/password/reset")
    class ResetPasswordTests {

        @Test
        @DisplayName("should reset password successfully and return success response")
        void resetPasswordSuccess() throws Exception {
            String token = "reset-token-123";
            String newPassword = "NewStrongPass!234";

            PasswordResetPerformRequest request =
                new PasswordResetPerformRequest(token, newPassword);

            doNothing().when(resetPasswordService)
                .resetPassword(any(ResetPasswordCommand.class));

            mockMvc.perform(post("/auth/password/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // PasswordResetResponse(success, message)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(
                    "Password has been reset successfully."
                ));

            // capture command
            ArgumentCaptor<ResetPasswordCommand> captor =
                ArgumentCaptor.forClass(ResetPasswordCommand.class);

            verify(resetPasswordService).resetPassword(captor.capture());
            ResetPasswordCommand cmd = captor.getValue();
            assertThat(cmd.token()).isEqualTo(token);
            assertThat(cmd.newPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("should return 400 RESET_FAILED when service throws IllegalArgumentException")
        void resetPasswordInvalidToken() throws Exception {
            PasswordResetPerformRequest request =
                new PasswordResetPerformRequest("invalid-token", "SomePassword123!");

            doThrow(new IllegalArgumentException("Invalid token"))
                .when(resetPasswordService)
                .resetPassword(any(ResetPasswordCommand.class));

            mockMvc.perform(post("/auth/password/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("RESET_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid token"));

            verify(resetPasswordService)
                .resetPassword(any(ResetPasswordCommand.class));
        }

        @Test
        @DisplayName("should return 400 RESET_FAILED when service throws IllegalStateException (e.g. expired token)")
        void resetPasswordExpiredToken() throws Exception {
            PasswordResetPerformRequest request =
                new PasswordResetPerformRequest("expired-token", "SomePassword123!");

            doThrow(new IllegalStateException("Token has expired"))
                .when(resetPasswordService)
                .resetPassword(any(ResetPasswordCommand.class));

            mockMvc.perform(post("/auth/password/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("RESET_FAILED"))
                .andExpect(jsonPath("$.message").value("Token has expired"));

            verify(resetPasswordService)
                .resetPassword(any(ResetPasswordCommand.class));
        }
    }
}

package com.estim.javaapi.controllers;

import com.estim.javaapi.application.password.RequestPasswordResetCommand;
import com.estim.javaapi.application.password.RequestPasswordResetService;
import com.estim.javaapi.application.password.ResetPasswordCommand;
import com.estim.javaapi.application.password.ResetPasswordService;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.password.PasswordResetPerformRequest;
import com.estim.javaapi.presentation.password.PasswordResetRequest;
import com.estim.javaapi.presentation.password.PasswordResetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password")
public class PasswordController {

    private final RequestPasswordResetService requestPasswordResetService;
    private final ResetPasswordService resetPasswordService;

    public PasswordController(RequestPasswordResetService requestPasswordResetService,
                              ResetPasswordService resetPasswordService) {
        this.requestPasswordResetService = requestPasswordResetService;
        this.resetPasswordService = resetPasswordService;
    }

    /**
     * POST /auth/password/reset-request
     *
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/reset-request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest request) {
        try {
            RequestPasswordResetCommand command =
                new RequestPasswordResetCommand(request.email());

            requestPasswordResetService.requestReset(command);

            // Do NOT reveal whether the email exists.
            PasswordResetResponse response = new PasswordResetResponse(
                true,
                "If an account exists for that email, a reset link has been sent."
            );
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("RESET_REQUEST_FAILED", ex.getMessage(), null));
        }
    }

    /**
     * POST /auth/password/reset
     *
     * Body: { "token": "...", "newPassword": "..." }
     */
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody PasswordResetPerformRequest request) {
        try {
            ResetPasswordCommand command =
                new ResetPasswordCommand(request.token(), request.newPassword());

            resetPasswordService.resetPassword(command);

            PasswordResetResponse response =
                new PasswordResetResponse(true, "Password has been reset successfully.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("RESET_FAILED", ex.getMessage(), null));
        }
    }
}

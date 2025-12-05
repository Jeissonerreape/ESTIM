package com.estim.javaapi.controllers;

import com.estim.javaapi.application.payment.AddPaymentMethodCommand;
import com.estim.javaapi.application.payment.AddPaymentMethodService;
import com.estim.javaapi.application.payment.ListPaymentMethodsService;
import com.estim.javaapi.application.payment.RemovePaymentMethodCommand;
import com.estim.javaapi.application.payment.RemovePaymentMethodService;
import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.common.ErrorResponse;
import com.estim.javaapi.presentation.common.PaymentMethodMapper;
import com.estim.javaapi.presentation.payment.PaymentMethodRequest;
import com.estim.javaapi.presentation.payment.PaymentMethodResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentMethodController {

    private final ListPaymentMethodsService listPaymentMethodsService;
    private final AddPaymentMethodService addPaymentMethodService;
    private final RemovePaymentMethodService removePaymentMethodService;
    private final JwtAuthenticationProvider authenticationProvider;
    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodController(ListPaymentMethodsService listPaymentMethodsService,
                                   AddPaymentMethodService addPaymentMethodService,
                                   RemovePaymentMethodService removePaymentMethodService,
                                   JwtAuthenticationProvider authenticationProvider,
                                   PaymentMethodMapper paymentMethodMapper) {

        this.listPaymentMethodsService = listPaymentMethodsService;
        this.addPaymentMethodService = addPaymentMethodService;
        this.removePaymentMethodService = removePaymentMethodService;
        this.authenticationProvider = authenticationProvider;
        this.paymentMethodMapper = paymentMethodMapper;
    }

    @GetMapping("/me/payment-methods")
    public ResponseEntity<?> list(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            var userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            List<PaymentMethod> methods =
                listPaymentMethodsService.list(userId.value().toString());

            List<PaymentMethodResponse> response =
                paymentMethodMapper.toResponseList(methods);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(401)
                .body(new ErrorResponse("UNAUTHORIZED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }

    @PostMapping("/me/payment-methods")
    public ResponseEntity<?> add(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @RequestBody PaymentMethodRequest request) {

        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            var userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            AddPaymentMethodCommand command = new AddPaymentMethodCommand(
                userId.value().toString(),
                request.provider(),
                request.token(),
                request.last4(),
                request.isDefault()
            );

            addPaymentMethodService.add(command);

            return ResponseEntity.status(201).build();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("PAYMENT_METHOD_ADD_FAILED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }

    @DeleteMapping("/me/payment-methods/{id}")
    public ResponseEntity<?> remove(
        @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
        @PathVariable("id") String paymentMethodId) {

        try {
            authenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

            var userId = SecurityContext.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("Not authenticated"));

            RemovePaymentMethodCommand command = new RemovePaymentMethodCommand(
                userId.value().toString(),
                paymentMethodId
            );

            removePaymentMethodService.remove(command);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("PAYMENT_METHOD_REMOVE_FAILED", ex.getMessage(), null));
        } finally {
            authenticationProvider.clearAuthentication();
        }
    }
}

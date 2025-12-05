package com.estim.javaapi.controllers;

import com.estim.javaapi.application.payment.AddPaymentMethodCommand;
import com.estim.javaapi.application.payment.AddPaymentMethodService;
import com.estim.javaapi.application.payment.ListPaymentMethodsService;
import com.estim.javaapi.application.payment.RemovePaymentMethodCommand;
import com.estim.javaapi.application.payment.RemovePaymentMethodService;
import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.common.PaymentMethodMapper;
import com.estim.javaapi.presentation.payment.PaymentMethodRequest;
import com.estim.javaapi.presentation.payment.PaymentMethodResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListPaymentMethodsService listPaymentMethodsService;

    @MockitoBean
    private AddPaymentMethodService addPaymentMethodService;

    @MockitoBean
    private RemovePaymentMethodService removePaymentMethodService;

    @MockitoBean
    private JwtAuthenticationProvider authenticationProvider;

    @MockitoBean
    private PaymentMethodMapper paymentMethodMapper;

    // ======================================================
    //           GET /me/payment-methods
    // ======================================================

    @Nested
    @DisplayName("GET /me/payment-methods")
    class ListPaymentMethodsTests {

        @Test
        @DisplayName("should list payment methods for authenticated user")
        void listPaymentMethodsSuccess() throws Exception {
            String header = "Bearer valid-token";

            // auth ok
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            // current user id
            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("44444444-4444-4444-4444-444444444444");
            when(currentUserId.value()).thenReturn(userUuid);

            // domain payment methods
            PaymentMethod pm1 = mock(PaymentMethod.class);
            PaymentMethod pm2 = mock(PaymentMethod.class);
            List<PaymentMethod> methods = List.of(pm1, pm2);

            when(listPaymentMethodsService.list(userUuid.toString()))
                .thenReturn(methods);

            // mapped DTOs
            PaymentMethodResponse resp1 = new PaymentMethodResponse(
                "pm-1", "VISA", "4242", true
            );
            PaymentMethodResponse resp2 = new PaymentMethodResponse(
                "pm-2", "MASTERCARD", "5555", false
            );

            when(paymentMethodMapper.toResponseList(methods))
                .thenReturn(List.of(resp1, resp2));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(get("/me/payment-methods")
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0].id").value("pm-1"))
                    .andExpect(jsonPath("$[0].provider").value("VISA"))
                    .andExpect(jsonPath("$[0].last4").value("4242"))
                    .andExpect(jsonPath("$[0].isDefault").value(true))
                    .andExpect(jsonPath("$[1].id").value("pm-2"))
                    .andExpect(jsonPath("$[1].provider").value("MASTERCARD"))
                    .andExpect(jsonPath("$[1].last4").value("5555"))
                    .andExpect(jsonPath("$[1].isDefault").value(false));

                verify(listPaymentMethodsService)
                    .list(userUuid.toString());
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 401 UNAUTHORIZED when auth fails (e.g. missing header)")
        void listPaymentMethodsUnauthorizedAuthFails() throws Exception {
            // auth fails when header is null
            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(get("/me/payment-methods"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(listPaymentMethodsService);
        }

        @Test
        @DisplayName("should return 401 UNAUTHORIZED when SecurityContext has no current user")
        void listPaymentMethodsNoCurrentUser() throws Exception {
            String header = "Bearer valid-token";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(get("/me/payment-methods")
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                    .andExpect(jsonPath("$.message").value("Not authenticated"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(listPaymentMethodsService);
        }
    }

    // ======================================================
    //           POST /me/payment-methods
    // ======================================================

    @Nested
    @DisplayName("POST /me/payment-methods")
    class AddPaymentMethodTests {

        @Test
        @DisplayName("should add payment method for authenticated user and return 201")
        void addPaymentMethodSuccess() throws Exception {
            String header = "Bearer valid-token";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("55555555-5555-5555-5555-555555555555");
            when(currentUserId.value()).thenReturn(userUuid);

            PaymentMethodRequest request = new PaymentMethodRequest(
                "VISA",
                "pm-token-123",    // externalToken
                "4242",
                true
            );

            doNothing().when(addPaymentMethodService)
                .add(any(AddPaymentMethodCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(post("/me/payment-methods")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated());

                ArgumentCaptor<AddPaymentMethodCommand> captor =
                    ArgumentCaptor.forClass(AddPaymentMethodCommand.class);

                verify(addPaymentMethodService).add(captor.capture());
                AddPaymentMethodCommand cmd = captor.getValue();

                assertThat(cmd.userId()).isEqualTo(userUuid.toString());
                assertThat(cmd.provider()).isEqualTo("VISA");
                assertThat(cmd.externalToken()).isEqualTo("pm-token-123");
                assertThat(cmd.last4()).isEqualTo("4242");
                assertThat(cmd.isDefault()).isTrue();
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 400 PAYMENT_METHOD_ADD_FAILED when auth fails")
        void addPaymentMethodAuthFails() throws Exception {
            PaymentMethodRequest request = new PaymentMethodRequest(
                "VISA", "token", "4242", true
            );

            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(post("/me/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PAYMENT_METHOD_ADD_FAILED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(addPaymentMethodService);
        }

        @Test
        @DisplayName("should return 400 PAYMENT_METHOD_ADD_FAILED when add service throws IllegalArgumentException")
        void addPaymentMethodServiceValidationError() throws Exception {
            String header = "Bearer valid-token";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("66666666-6666-6666-6666-666666666666");
            when(currentUserId.value()).thenReturn(userUuid);

            PaymentMethodRequest request = new PaymentMethodRequest(
                "",   // invalid provider
                "token",
                "4242",
                true
            );

            doThrow(new IllegalArgumentException("Invalid provider"))
                .when(addPaymentMethodService)
                .add(any(AddPaymentMethodCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(post("/me/payment-methods")
                        .header("Authorization", header)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PAYMENT_METHOD_ADD_FAILED"))
                    .andExpect(jsonPath("$.message").value("Invalid provider"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }
    }

    // ======================================================
    //        DELETE /me/payment-methods/{id}
    // ======================================================

    @Nested
    @DisplayName("DELETE /me/payment-methods/{id}")
    class RemovePaymentMethodTests {

        @Test
        @DisplayName("should remove payment method for authenticated user and return 204")
        void removePaymentMethodSuccess() throws Exception {
            String header = "Bearer valid-token";
            String paymentMethodId = "pm-123";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("77777777-7777-7777-7777-777777777777");
            when(currentUserId.value()).thenReturn(userUuid);

            doNothing().when(removePaymentMethodService)
                .remove(any(RemovePaymentMethodCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(delete("/me/payment-methods/{id}", paymentMethodId)
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isNoContent());

                ArgumentCaptor<RemovePaymentMethodCommand> captor =
                    ArgumentCaptor.forClass(RemovePaymentMethodCommand.class);

                verify(removePaymentMethodService).remove(captor.capture());
                RemovePaymentMethodCommand cmd = captor.getValue();
                assertThat(cmd.userId()).isEqualTo(userUuid.toString());
                assertThat(cmd.paymentMethodId()).isEqualTo(paymentMethodId);
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 400 PAYMENT_METHOD_REMOVE_FAILED when auth fails")
        void removePaymentMethodAuthFails() throws Exception {
            String paymentMethodId = "pm-123";

            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(delete("/me/payment-methods/{id}", paymentMethodId))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PAYMENT_METHOD_REMOVE_FAILED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(removePaymentMethodService);
        }

        @Test
        @DisplayName("should return 400 PAYMENT_METHOD_REMOVE_FAILED when service throws IllegalArgumentException")
        void removePaymentMethodServiceValidationError() throws Exception {
            String header = "Bearer valid-token";
            String paymentMethodId = "pm-invalid";

            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId currentUserId = mock(UserId.class);
            UUID userUuid = UUID.fromString("88888888-8888-8888-8888-888888888888");
            when(currentUserId.value()).thenReturn(userUuid);

            doThrow(new IllegalArgumentException("Payment method not found"))
                .when(removePaymentMethodService)
                .remove(any(RemovePaymentMethodCommand.class));

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(currentUserId));

                mockMvc.perform(delete("/me/payment-methods/{id}", paymentMethodId)
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("PAYMENT_METHOD_REMOVE_FAILED"))
                    .andExpect(jsonPath("$.message").value("Payment method not found"));
            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
        }
    }
}

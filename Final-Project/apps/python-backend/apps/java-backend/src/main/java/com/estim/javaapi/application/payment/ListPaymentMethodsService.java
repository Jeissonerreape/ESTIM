package com.estim.javaapi.application.payment;

import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Application service for listing the payment methods of a user.
 */
@Service
public class ListPaymentMethodsService {

    private final UserRepository userRepository;

    public ListPaymentMethodsService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public List<PaymentMethod> list(String userId) {
        UserId id = new UserId(UUID.fromString(userId));

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.paymentMethods();
    }
}

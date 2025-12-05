package com.estim.javaapi.application.payment;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.PaymentMethodId;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for removing a payment method from a user.
 */
@Service
public class RemovePaymentMethodService {

    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;

    public RemovePaymentMethodService(UserRepository userRepository,
                                      DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void remove(RemovePaymentMethodCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        PaymentMethodId paymentMethodId = new PaymentMethodId(UUID.fromString(command.paymentMethodId()));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.removePaymentMethod(paymentMethodId);

        userRepository.save(user);

        // Publish events raised by the aggregate (e.g. PaymentMethodRemoved)
        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}

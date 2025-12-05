package com.estim.javaapi.application.payment;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.user.PaymentMethod;
import com.estim.javaapi.domain.user.PaymentProvider;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Application service for adding a payment method to a user.
 */
@Service
public class AddPaymentMethodService {

    private final UserRepository userRepository;
    private final PaymentProviderClient paymentProviderClient;
    private final DomainEventPublisher eventPublisher;

    public AddPaymentMethodService(UserRepository userRepository,
                                   PaymentProviderClient paymentProviderClient,
                                   DomainEventPublisher eventPublisher) {

        this.userRepository = Objects.requireNonNull(userRepository);
        this.paymentProviderClient = Objects.requireNonNull(paymentProviderClient);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void add(AddPaymentMethodCommand command) {
        UserId userId = new UserId(UUID.fromString(command.userId()));
        PaymentProvider provider = PaymentProvider.valueOf(command.provider().toUpperCase());

        // Optionally validate token with external provider
        paymentProviderClient.validateToken(provider.name(), command.externalToken());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentMethod method = PaymentMethod.newMethod(
            provider,
            command.externalToken(),
            command.last4(),
            command.isDefault()
        );

        user.addPaymentMethod(method);

        userRepository.save(user);

        // Publish events raised by the aggregate (e.g. PaymentMethodAdded)
        eventPublisher.publishAll(user.domainEvents());
        user.clearDomainEvents();
    }
}

package com.estim.javaapi.domain.user;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for user payment methods, if stored in a separate table
 * rather than entirely inside the User aggregate persistence.
 */
public interface PaymentMethodRepository {

    /**
     * Returns all payment methods belonging to the given user.
     */
    List<PaymentMethod> findByUserId(UserId userId);

    /**
     * Returns a single payment method by its id, if it exists.
     */
    Optional<PaymentMethod> findById(PaymentMethodId id);

    /**
     * Persists the given payment method for the specified user.
     * Implementations may either:
     *  - store userId as a foreign key on the payment_methods table, or
     *  - embed it in the aggregate if using another strategy.
     */
    PaymentMethod save(UserId userId, PaymentMethod method);

    /**
     * Deletes a payment method by id.
     */
    void deleteById(PaymentMethodId id);
}

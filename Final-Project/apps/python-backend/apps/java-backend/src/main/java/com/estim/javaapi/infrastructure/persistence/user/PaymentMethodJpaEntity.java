package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.PaymentProvider;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_payment_methods")
public class PaymentMethodJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private PaymentProvider provider;

    @Column(name = "external_token", nullable = false, length = 255)
    private String externalToken;

    @Column(name = "last4", nullable = false, length = 4)
    private String last4;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    protected PaymentMethodJpaEntity() {
        // for JPA
    }

    // Getters & setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}

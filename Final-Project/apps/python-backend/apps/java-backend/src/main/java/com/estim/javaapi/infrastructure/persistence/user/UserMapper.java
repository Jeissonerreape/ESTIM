package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.*;
import org.springframework.stereotype.Component;  // <--- add this import

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserMapper {

    // ---------- Domain -> JPA ----------

    public UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();

        entity.setId(user.id().value());
        entity.setEmail(user.email().value());
        entity.setPasswordHash(user.passwordHash().value());
        entity.setStatus(user.status());
        entity.setEmailVerified(user.emailVerified());
        entity.setCreatedAt(user.createdAt());
        entity.setUpdatedAt(user.updatedAt());
        entity.setLastLoginAt(user.lastLoginAt());

        UserProfile profile = user.profile();
        if (profile != null) {
            entity.setDisplayName(profile.displayName());
            entity.setAvatarUrl(profile.avatarUrl());
            entity.setBio(profile.bio());
            entity.setLocation(profile.location());

            PrivacySettings privacy = profile.privacySettings();
            if (privacy != null) {
                entity.setPrivacyShowProfile(privacy.showProfile());
                entity.setPrivacyShowActivity(privacy.showActivity());
                entity.setPrivacyShowWishlist(privacy.showWishlist());
            }
        }

        List<PaymentMethodJpaEntity> paymentMethodEntities = new ArrayList<>();
        for (PaymentMethod pm : user.paymentMethods()) {
            PaymentMethodJpaEntity pmEntity = new PaymentMethodJpaEntity();
            pmEntity.setId(pm.id().value());
            pmEntity.setProvider(pm.provider());
            pmEntity.setExternalToken(pm.externalToken());
            pmEntity.setLast4(pm.last4());
            pmEntity.setDefault(pm.isDefault());
            pmEntity.setUser(entity);
            paymentMethodEntities.add(pmEntity);
        }
        entity.setPaymentMethods(paymentMethodEntities);

        List<OAuthAccountJpaEntity> oauthEntities = new ArrayList<>();
        for (OAuthAccount oa : user.linkedAccounts()) {
            OAuthAccountJpaEntity oaEntity = new OAuthAccountJpaEntity();
            oaEntity.setId(oa.id().value());
            oaEntity.setProvider(oa.provider());
            oaEntity.setExternalUserId(oa.externalUserId());
            oaEntity.setEmail(oa.email());
            oaEntity.setLinkedAt(oa.linkedAt());
            oaEntity.setUser(entity);
            oauthEntities.add(oaEntity);
        }
        entity.setLinkedAccounts(oauthEntities);

        return entity;
    }

    // ---------- JPA -> Domain ----------

    public User toDomain(UserJpaEntity entity) {
        UserId id = new UserId(entity.getId());
        Email email = new Email(entity.getEmail());
        PasswordHash passwordHash = new PasswordHash(entity.getPasswordHash());
        UserStatus status = entity.getStatus();
        boolean emailVerified = entity.isEmailVerified();

        PrivacySettings privacy = new PrivacySettings(
            entity.isPrivacyShowProfile(),
            entity.isPrivacyShowActivity(),
            entity.isPrivacyShowWishlist()
        );

        UserProfile profile = new UserProfile(
            entity.getDisplayName(),
            entity.getAvatarUrl(),
            entity.getBio(),
            entity.getLocation(),
            privacy
        );

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        for (PaymentMethodJpaEntity pmEntity : entity.getPaymentMethods()) {
            PaymentMethod pm = new PaymentMethod(
                new PaymentMethodId(pmEntity.getId()),
                pmEntity.getProvider(),
                pmEntity.getExternalToken(),
                pmEntity.getLast4(),
                pmEntity.isDefault()
            );
            paymentMethods.add(pm);
        }

        List<OAuthAccount> oauthAccounts = new ArrayList<>();
        for (OAuthAccountJpaEntity oaEntity : entity.getLinkedAccounts()) {
            OAuthAccount oa = new OAuthAccount(
                new OAuthAccountId(oaEntity.getId()),
                oaEntity.getProvider(),
                oaEntity.getExternalUserId(),
                oaEntity.getEmail(),
                oaEntity.getLinkedAt()
            );
            oauthAccounts.add(oa);
        }

        Instant createdAt = entity.getCreatedAt();
        Instant updatedAt = entity.getUpdatedAt();
        Instant lastLoginAt = entity.getLastLoginAt();

        return instantiateUser(
            id,
            email,
            passwordHash,
            status,
            emailVerified,
            profile,
            paymentMethods,
            oauthAccounts,
            createdAt,
            updatedAt,
            lastLoginAt
        );
    }

    private User instantiateUser(
        UserId id,
        Email email,
        PasswordHash passwordHash,
        UserStatus status,
        boolean emailVerified,
        UserProfile profile,
        List<PaymentMethod> paymentMethods,
        List<OAuthAccount> linkedAccounts,
        Instant createdAt,
        Instant updatedAt,
        Instant lastLoginAt
    ) {
        try {
            Constructor<User> ctor = User.class.getDeclaredConstructor(
                UserId.class,
                Email.class,
                PasswordHash.class,
                UserStatus.class,
                boolean.class,
                UserProfile.class,
                List.class,
                List.class,
                Instant.class,
                Instant.class,
                Instant.class
            );
            ctor.setAccessible(true);
            return ctor.newInstance(
                id,
                email,
                passwordHash,
                status,
                emailVerified,
                profile,
                paymentMethods,
                linkedAccounts,
                createdAt,
                updatedAt,
                lastLoginAt
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate User from JPA entity via reflection", e);
        }
    }
}

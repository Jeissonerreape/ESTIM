package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.PasswordResetToken;
import com.estim.javaapi.domain.user.PasswordResetTokenId;
import com.estim.javaapi.domain.user.PasswordResetTokenRepository;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final PasswordResetTokenJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;

    public PasswordResetTokenRepositoryImpl(PasswordResetTokenJpaRepository jpaRepository,
                                            UserJpaRepository userJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = toEntity(token);
        PasswordResetTokenJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
            .map(this::toDomain);
    }

    @Override
    public Optional<PasswordResetToken> findByTokenId(PasswordResetTokenId tokenId) {
        return jpaRepository.findById(tokenId.value())
            .map(this::toDomain);
    }

    private PasswordResetTokenJpaEntity toEntity(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.setId(token.id().value());

        UserJpaEntity userEntity = userJpaRepository.findById(token.userId().value())
            .orElseThrow(() -> new IllegalStateException("User not found for password reset token"));

        entity.setUser(userEntity);
        entity.setToken(token.token());
        entity.setExpiresAt(token.expiresAt());
        entity.setUsed(token.used());

        return entity;
    }

    private PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        PasswordResetTokenId id = new PasswordResetTokenId(entity.getId());
        UserId userId = new UserId(entity.getUser().getId());

        return new PasswordResetToken(
            id,
            userId,
            entity.getToken(),
            entity.getExpiresAt(),
            entity.isUsed()
        );
    }
}

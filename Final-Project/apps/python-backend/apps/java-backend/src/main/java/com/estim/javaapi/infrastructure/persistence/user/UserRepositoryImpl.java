package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.OAuthProvider;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
            .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByDisplayName(String displayName) {
        return jpaRepository.existsByDisplayName(displayName);
    }

    @Override
    public Optional<User> findByOAuthProviderAndExternalId(OAuthProvider provider, String externalUserId) {
        return jpaRepository.findByOAuthProviderAndExternalUserId(provider, externalUserId)
            .map(mapper::toDomain);
    }
}

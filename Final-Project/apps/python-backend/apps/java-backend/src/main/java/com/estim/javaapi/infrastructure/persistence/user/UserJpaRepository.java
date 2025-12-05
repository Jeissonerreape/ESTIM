package com.estim.javaapi.infrastructure.persistence.user;

import com.estim.javaapi.domain.user.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // NEW: username/displayName uniqueness
    boolean existsByDisplayName(String displayName);

    @Query("""
        SELECT u
        FROM UserJpaEntity u
        JOIN u.linkedAccounts a
        WHERE a.provider = :provider
          AND a.externalUserId = :externalUserId
    """)
    Optional<UserJpaEntity> findByOAuthProviderAndExternalUserId(
        @Param("provider") OAuthProvider provider,
        @Param("externalUserId") String externalUserId
    );
}


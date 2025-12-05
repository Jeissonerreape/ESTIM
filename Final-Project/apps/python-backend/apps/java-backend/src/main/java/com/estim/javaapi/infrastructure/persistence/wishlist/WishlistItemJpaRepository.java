package com.estim.javaapi.infrastructure.persistence.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistItemJpaRepository extends JpaRepository<WishlistItemJpaEntity, UUID> {

    List<WishlistItemJpaEntity> findByUserId(UUID userId);

    Optional<WishlistItemJpaEntity> findByUserIdAndGameId(UUID userId, UUID gameId);

    boolean existsByUserIdAndGameId(UUID userId, UUID gameId);

    List<WishlistItemJpaEntity> findByGameId(UUID gameId);
}

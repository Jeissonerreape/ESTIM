package com.estim.javaapi.infrastructure.persistence.wishlist;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.domain.wishlist.WishlistRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WishlistRepositoryImpl implements WishlistRepository {

    private final WishlistItemJpaRepository jpaRepository;

    public WishlistRepositoryImpl(WishlistItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<WishlistItem> findByUserId(UserId userId) {
        UUID userUuid = userId.value(); // ✔ UserId → value()
        return jpaRepository.findByUserId(userUuid)
            .stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public Optional<WishlistItem> findByUserIdAndGameId(UserId userId, GameId gameId) {
        UUID userUuid = userId.value();     // ✔
        UUID gameUuid = gameId.getValue();  // ✔ GameId → getValue()

        return jpaRepository.findByUserIdAndGameId(userUuid, gameUuid)
            .map(this::toDomain);
    }

    @Override
    public List<WishlistItem> findByGameId(GameId gameId) {
        UUID gameUuid = gameId.getValue(); // GameId → getValue()
        return jpaRepository.findByGameId(gameUuid)
            .stream()
            .map(this::toDomain)
            .toList();
    }


    @Override
    public void save(WishlistItem item) {
        UUID userUuid = item.getUserId().value();      // UserId → value()
        UUID gameUuid = item.getGameId().getValue();   // GameId → getValue()

        var existing = jpaRepository.findByUserIdAndGameId(userUuid, gameUuid);

        WishlistItemJpaEntity entity = existing.orElseGet(() ->
            new WishlistItemJpaEntity(
                userUuid,
                gameUuid,
                item.getAddedAt()
            )
        );

        // keep addedAt in sync (if you want)
        entity.setAddedAt(item.getAddedAt());

        // NO priceWhenAdded here anymore – it's not in the entity nor in the table
        jpaRepository.save(entity);
    }

    @Override
    public void delete(WishlistItem item) {
        UUID userUuid = item.getUserId().value();     // ✔
        UUID gameUuid = item.getGameId().getValue();  // ✔

        jpaRepository.findByUserIdAndGameId(userUuid, gameUuid)
            .ifPresent(jpaRepository::delete);
    }

    @Override
    public boolean existsByUserIdAndGameId(UserId userId, GameId gameId) {
        return jpaRepository.existsByUserIdAndGameId(
            userId.value(),       // ✔
            gameId.getValue()     // ✔
        );
    }

    private WishlistItem toDomain(WishlistItemJpaEntity entity) {
        return WishlistItem.of(
            new UserId(entity.getUserId()),
            new GameId(entity.getGameId()),
            entity.getAddedAt(),
            null,                  // priceWhenAdded not stored in DB
            Collections.emptyMap() // notificationPreferences not persisted yet
        );
    }
}

package com.estim.javaapi.infrastructure.persistence.library;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LibraryEntryJpaRepository extends JpaRepository<LibraryEntryJpaEntity, UUID> {

    Optional<LibraryEntryJpaEntity> findByUserIdAndGameId(UUID userId, UUID gameId);

    List<LibraryEntryJpaEntity> findByUserId(UUID userId);
}

package com.estim.javaapi.infrastructure.persistence.library;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.library.LibraryRepository;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LibraryRepositoryImpl implements LibraryRepository {

    private final LibraryEntryJpaRepository jpaRepository;

    public LibraryRepositoryImpl(LibraryEntryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<LibraryEntry> findById(LibraryEntryId id) {
        return jpaRepository.findById(id.getValue())
            .map(LibraryEntryMapper::toDomain);
    }

    @Override
    public Optional<LibraryEntry> findByUserAndGame(UserId userId, GameId gameId) {
        return jpaRepository.findByUserIdAndGameId(userId.value(), gameId.getValue())
            .map(LibraryEntryMapper::toDomain);
    }

    @Override
    public List<LibraryEntry> findByUser(UserId userId) {
        return jpaRepository.findByUserId(userId.value())
            .stream()
            .map(LibraryEntryMapper::toDomain)
            .toList();
    }

    @Override
    public LibraryEntry save(LibraryEntry entry) {
        LibraryEntryJpaEntity entity = LibraryEntryMapper.toEntity(entry);
        LibraryEntryJpaEntity saved = jpaRepository.save(entity);
        return LibraryEntryMapper.toDomain(saved);
    }
}

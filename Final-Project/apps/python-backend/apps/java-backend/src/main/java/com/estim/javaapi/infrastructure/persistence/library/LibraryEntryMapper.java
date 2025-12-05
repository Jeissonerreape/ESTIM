package com.estim.javaapi.infrastructure.persistence.library;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.domain.user.UserId;

public final class LibraryEntryMapper {

    private LibraryEntryMapper() {
    }

    public static LibraryEntry toDomain(LibraryEntryJpaEntity entity) {
        return new LibraryEntry(
            LibraryEntryId.of(entity.getId()),
            new UserId(entity.getUserId()),
            GameId.of(entity.getGameId()),
            LibraryEntrySource.fromDatabaseValue(entity.getSource()),
            entity.getAddedAt()
        );
    }

    public static LibraryEntryJpaEntity toEntity(LibraryEntry entry) {
        LibraryEntryJpaEntity entity = new LibraryEntryJpaEntity();
        entity.setId(entry.getId().getValue());
        entity.setUserId(entry.getUserId().value());
        entity.setGameId(entry.getGameId().getValue());
        entity.setSource(entry.getSource() != null ? entry.getSource().toDatabaseValue() : null);
        entity.setAddedAt(entry.getAddedAt());
        return entity;
    }
}

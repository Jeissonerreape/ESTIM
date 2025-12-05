package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntryStatus;
import com.estim.javaapi.domain.library.LibraryRepository;
import com.estim.javaapi.domain.library.events.GameAddedToLibrary;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Application service for adding a game to a user's library.
 */
@Service
public class AddGameToLibraryService {

    private final LibraryRepository libraryRepository;
    private final DomainEventPublisher eventPublisher;

    public AddGameToLibraryService(LibraryRepository libraryRepository,
                                   DomainEventPublisher eventPublisher) {
        this.libraryRepository = libraryRepository;
        this.eventPublisher = eventPublisher;
    }

    public LibraryEntry addGameToLibrary(AddGameToLibraryCommand command) {
        UserId userId = command.userId();
        GameId gameId = command.gameId();

        libraryRepository.findByUserAndGame(userId, gameId).ifPresent(existing -> {
            throw new IllegalStateException("Game is already in user's library");
        });

        LibraryEntry entry = LibraryEntry.newEntry(
            userId,
            gameId,
            command.source(),
            Instant.now()
        );

        LibraryEntry saved = libraryRepository.save(entry);

        eventPublisher.publish(new GameAddedToLibrary(
            saved.getId(),
            saved.getUserId(),
            saved.getGameId()
        ));

        return saved;
    }
}

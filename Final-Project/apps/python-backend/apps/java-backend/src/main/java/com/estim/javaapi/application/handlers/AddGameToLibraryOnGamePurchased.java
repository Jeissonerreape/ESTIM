package com.estim.javaapi.application.handlers;

import com.estim.javaapi.application.library.AddGameToLibraryCommand;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.application.library.AddGameToLibraryService;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Handler that reacts when a game purchase is completed and
 * adds the game to the user's library.
 */
@Component
public class AddGameToLibraryOnGamePurchased {

    private final AddGameToLibraryService addGameToLibraryService;

    public AddGameToLibraryOnGamePurchased(AddGameToLibraryService addGameToLibraryService) {
        this.addGameToLibraryService = addGameToLibraryService;
    }

    /**
     * Replace GamePurchased with your actual purchase domain event type
     * when you implement the ordering/purchase context.
     */
    @EventListener
    public void handle(GamePurchased event) {
        UserId userId = event.userId();
        GameId gameId = GameId.fromString(event.gameId());

        AddGameToLibraryCommand command = new AddGameToLibraryCommand(
            userId,
            gameId,
            LibraryEntrySource.PURCHASE
        );

        addGameToLibraryService.addGameToLibrary(command);
    }

    /**
     * Placeholder event type. Replace with your real purchase event.
     */
    public interface GamePurchased {
        UserId userId();
        String gameId(); // string UUID from DB / message
    }
}

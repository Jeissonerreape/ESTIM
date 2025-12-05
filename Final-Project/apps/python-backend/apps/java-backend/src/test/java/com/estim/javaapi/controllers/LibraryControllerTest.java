package com.estim.javaapi.controllers;

import com.estim.javaapi.application.library.AddGameToLibraryCommand;
import com.estim.javaapi.application.library.AddGameToLibraryService;
import com.estim.javaapi.application.library.ListUserLibraryQuery;
import com.estim.javaapi.application.library.ListUserLibraryService;
import com.estim.javaapi.application.library.UpdateLibraryEntryService;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.library.LibraryEntry;
import com.estim.javaapi.domain.library.LibraryEntryId;
import com.estim.javaapi.domain.library.LibraryEntrySource;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.infrastructure.security.AuthenticatedUser;
import com.estim.javaapi.presentation.library.AddGameToLibraryRequest;
import com.estim.javaapi.presentation.library.LibraryEntryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LibraryController}.
 *
 * These tests exercise the controller logic assuming that:
 * - Spring Security has already authenticated the request
 * - @AuthenticationPrincipal injects an AuthenticatedUser
 */
@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private ListUserLibraryService listUserLibraryService;

    @Mock
    private UpdateLibraryEntryService updateLibraryEntryService;

    @Mock
    private AddGameToLibraryService addGameToLibraryService;

    private LibraryController controller;

    @BeforeEach
    void setUp() {
        controller = new LibraryController(
            listUserLibraryService,
            updateLibraryEntryService,
            addGameToLibraryService
        );
    }

    @Test
    void getMyLibrary_returnsEntriesForAuthenticatedUser() {
        // Arrange
        UUID rawUserId = UUID.randomUUID();
        UserId userId = new UserId(rawUserId);
        AuthenticatedUser principal = new AuthenticatedUser(userId);

        UUID libraryId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        Instant addedAt = Instant.parse("2025-01-01T00:00:00Z");

        LibraryEntryResponse entryResponse = new LibraryEntryResponse(
            libraryId,
            gameId,
            "Cyberpunk Legends",
            "https://example.com/cover.jpg",
            "PURCHASE",
            addedAt
        );

        ListUserLibraryQuery expectedQuery = new ListUserLibraryQuery(userId);
        when(listUserLibraryService.listUserLibrary(expectedQuery))
            .thenReturn(List.of(entryResponse));

        // Act
        ResponseEntity<?> response = controller.getMyLibrary(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        @SuppressWarnings("unchecked")
        List<LibraryEntryResponse> body = (List<LibraryEntryResponse>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());

        LibraryEntryResponse first = body.get(0);
        assertEquals(gameId, first.gameId());
        assertEquals("Cyberpunk Legends", first.gameTitle());
        assertEquals("https://example.com/cover.jpg", first.coverImageUrl());
        assertEquals("PURCHASE", first.source());
        assertEquals(addedAt, first.addedAt());

        verify(listUserLibraryService).listUserLibrary(expectedQuery);
        verifyNoInteractions(updateLibraryEntryService, addGameToLibraryService);
    }

    @Test
    void addGameToLibrary_createsEntryForAuthenticatedUser() {
        // Arrange
        UUID rawUserId = UUID.randomUUID();
        UserId userId = new UserId(rawUserId);
        AuthenticatedUser principal = new AuthenticatedUser(userId);

        UUID rawGameId = UUID.randomUUID();
        Instant addedAt = Instant.parse("2025-02-01T00:00:00Z");

        // Domain entry returned by the service
        LibraryEntry createdEntry = new LibraryEntry(
            LibraryEntryId.randomId(),
            userId,
            GameId.of(rawGameId),
            LibraryEntrySource.PURCHASE,
            addedAt
        );

        when(addGameToLibraryService.addGameToLibrary(any(AddGameToLibraryCommand.class)))
            .thenReturn(createdEntry);

        AddGameToLibraryRequest request = new AddGameToLibraryRequest(
            rawGameId,
            "PURCHASE"
        );

        // Act
        ResponseEntity<?> response = controller.addGameToLibrary(principal, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof LibraryEntryResponse);

        LibraryEntryResponse body = (LibraryEntryResponse) response.getBody();
        assertEquals(rawGameId, body.gameId());
        assertEquals("PURCHASE", body.source());
        assertEquals(addedAt, body.addedAt());

        // Verify command sent to service
        ArgumentCaptor<AddGameToLibraryCommand> captor =
            ArgumentCaptor.forClass(AddGameToLibraryCommand.class);
        verify(addGameToLibraryService).addGameToLibrary(captor.capture());

        AddGameToLibraryCommand sentCommand = captor.getValue();
        assertEquals(userId, sentCommand.userId());
        assertEquals(rawGameId, sentCommand.gameId().getValue());
        assertEquals(LibraryEntrySource.PURCHASE, sentCommand.source());

        verifyNoInteractions(listUserLibraryService, updateLibraryEntryService);
    }

    @Test
    void getMyLibrary_returnsUnauthorizedWhenPrincipalIsNull() {
        // Act
        ResponseEntity<?> response = controller.getMyLibrary(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        verifyNoInteractions(listUserLibraryService, updateLibraryEntryService, addGameToLibraryService);
    }

    @Test
    void addGameToLibrary_returnsUnauthorizedWhenPrincipalIsNull() {
        // Arrange
        AddGameToLibraryRequest request = new AddGameToLibraryRequest(
            UUID.randomUUID(),
            "PURCHASE"
        );

        // Act
        ResponseEntity<?> response = controller.addGameToLibrary(null, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(addGameToLibraryService, listUserLibraryService, updateLibraryEntryService);
    }
}

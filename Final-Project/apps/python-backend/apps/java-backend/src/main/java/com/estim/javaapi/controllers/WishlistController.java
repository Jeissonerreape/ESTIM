package com.estim.javaapi.controllers;

import com.estim.javaapi.application.wishlist.AddToWishlistCommand;
import com.estim.javaapi.application.wishlist.AddToWishlistService;
import com.estim.javaapi.application.wishlist.ListWishlistForUserQuery;
import com.estim.javaapi.application.wishlist.ListWishlistService;
import com.estim.javaapi.application.wishlist.RemoveFromWishlistCommand;
import com.estim.javaapi.application.wishlist.RemoveFromWishlistService;
import com.estim.javaapi.application.wishlist.UpdateWishlistItemCommand;
import com.estim.javaapi.application.wishlist.UpdateWishlistItemService;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.wishlist.WishlistItem;
import com.estim.javaapi.infrastructure.security.AuthenticatedUser;
import com.estim.javaapi.presentation.wishlist.WishlistItemRequest;
import com.estim.javaapi.presentation.wishlist.WishlistItemResponse;
import com.estim.javaapi.presentation.wishlist.WishlistMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the authenticated user's wishlist.
 *
 * Base path: /me/wishlist
 */
@RestController
@RequestMapping("/me/wishlist")
public class WishlistController {

    private final AddToWishlistService addToWishlistService;
    private final RemoveFromWishlistService removeFromWishlistService;
    private final ListWishlistService listWishlistService;
    private final UpdateWishlistItemService updateWishlistItemService;
    private final WishlistMapper wishlistMapper;

    public WishlistController(AddToWishlistService addToWishlistService,
                              RemoveFromWishlistService removeFromWishlistService,
                              ListWishlistService listWishlistService,
                              UpdateWishlistItemService updateWishlistItemService,
                              WishlistMapper wishlistMapper) {
        this.addToWishlistService = addToWishlistService;
        this.removeFromWishlistService = removeFromWishlistService;
        this.listWishlistService = listWishlistService;
        this.updateWishlistItemService = updateWishlistItemService;
        this.wishlistMapper = wishlistMapper;
    }

    /**
     * GET /me/wishlist
     *
     * Lists all wishlist items for the current user.
     */
    @GetMapping
    public List<WishlistItemResponse> getWishlist(
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        UserId userId = currentUser.userId();
        return listWishlistService.listWishlist(new ListWishlistForUserQuery(userId));
    }

    /**
     * POST /me/wishlist
     *
     * Adds a game to the wishlist for the current user.
     * Body: { "gameId": "uuid-string", "notificationPreferences": { ... } }
     *
     * Returns 201 with the created wishlist item.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemResponse addToWishlist(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @RequestBody WishlistItemRequest request
    ) {
        UserId userId = currentUser.userId();
        GameId gameId = GameId.fromString(request.gameId());

        // 1) Add to wishlist
        addToWishlistService.addToWishlist(
            new AddToWishlistCommand(userId, gameId)
        );

        // 2) Optionally update notification preferences
        Map<String, Boolean> prefs = request.notificationPreferences();
        if (prefs != null && !prefs.isEmpty()) {
            updateWishlistItemService.updateWishlistItem(
                new UpdateWishlistItemCommand(userId, gameId, prefs)
            );
        }

        // 3) Reload enriched view and return the created item
        String gameIdStr = gameId.getValue().toString();
        return listWishlistService.listWishlist(new ListWishlistForUserQuery(userId))
            .stream()
            .filter(it -> it.gameId().equals(gameIdStr))
            .findFirst()
            .orElseThrow(); // should not happen if services behave correctly
    }

    /**
     * DELETE /me/wishlist/{gameId}
     *
     * Removes a game from the wishlist for the current user.
     */
    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWishlist(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @PathVariable("gameId") String gameIdStr
    ) {
        UserId userId = currentUser.userId();
        GameId gameId = GameId.fromString(gameIdStr);

        removeFromWishlistService.removeFromWishlist(
            new RemoveFromWishlistCommand(userId, gameId)
        );
    }

    // ---------- helpers for now (no GameRepository yet) ----------

    private String defaultGameTitle(WishlistItem item) {
        // You can tweak this to any placeholder you want
        String shortId = item.getGameId().getValue().toString().substring(0, 8);
        return "Wishlist game " + shortId;
    }

    private String defaultCoverImageUrl() {
        // Same placeholder you were using in the frontend
        return "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop";
    }
}

package com.estim.javaapi.application.wishlist;

import com.estim.javaapi.domain.user.UserId;

import java.util.Objects;

/**
 * Query to list all wishlist items for the given user.
 */
public final class ListWishlistForUserQuery {

    private final UserId userId;

    public ListWishlistForUserQuery(UserId userId) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
    }

    public UserId getUserId() {
        return userId;
    }
}

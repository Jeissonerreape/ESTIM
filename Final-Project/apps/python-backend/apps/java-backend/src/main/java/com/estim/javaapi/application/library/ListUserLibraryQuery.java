package com.estim.javaapi.application.library;

import com.estim.javaapi.domain.user.UserId;

/**
 * Query to list all library entries for a given user.
 */
public record ListUserLibraryQuery(
    UserId userId
) {
}

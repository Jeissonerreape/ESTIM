package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.library.GameId;

public record ListPostsQuery(
    PostType type,         // nullable – if null, all types
    GameId gameId,         // nullable – if null, not filtered by game
    String tag,            // nullable
    String searchText,     // nullable
    boolean onlyPublished, // usually true for public listing
    int limit,
    int offset
) {}

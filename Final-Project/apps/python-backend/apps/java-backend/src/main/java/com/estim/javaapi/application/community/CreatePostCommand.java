package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Set;

public record CreatePostCommand(
    UserId authorId,
    String title,
    String body,
    PostType type,
    GameId gameId,          // optional, can be null
    Set<String> tags,       // optional, can be null/empty
    boolean publishNow,     // if true, create as PUBLISHED
    boolean pinned          // initial pinned state
) {}

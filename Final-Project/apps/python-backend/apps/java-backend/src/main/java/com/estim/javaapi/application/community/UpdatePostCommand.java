package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.community.PostStatus;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.Set;

public record UpdatePostCommand(
    CommunityPostId postId,
    UserId editorId,           // used for authorization in the future
    String title,              // nullable – if null, keep current
    String body,               // nullable – if null, keep current
    Set<String> tags,          // nullable – if null, keep current
    Boolean pinned,            // nullable – if null, keep current
    PostStatus status,         // nullable – if null, keep current
    GameId gameId              // nullable – if null, keep current
) {}

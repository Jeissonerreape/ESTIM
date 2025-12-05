package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.user.UserId;

public record UpdateCommentCommand(
    CommentId commentId,
    UserId editorId, // for authorization later
    String body
) {}

package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.user.UserId;

public record DeleteCommentCommand(
    CommentId commentId,
    UserId requesterId
) {}

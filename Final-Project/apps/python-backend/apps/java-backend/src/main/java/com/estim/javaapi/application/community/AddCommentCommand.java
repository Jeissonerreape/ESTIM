package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.user.UserId;

public record AddCommentCommand(
    CommunityPostId postId,
    UserId authorId,
    String body
) {}

package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.user.UserId;

public record DeletePostCommand(
    CommunityPostId postId,
    UserId requesterId // for authorization later
) {}

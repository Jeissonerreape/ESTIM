package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.user.UserId;

public record ListUserPostsQuery(
    UserId authorId,
    PostType type, // nullable
    int limit,
    int offset
) {}

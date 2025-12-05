package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.CommunityPostId;

public record GetPostDetailsQuery(
    CommunityPostId postId
) {}

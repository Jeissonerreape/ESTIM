package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.community.Comment;
import com.estim.javaapi.domain.community.CommunityPost;

import java.util.List;

public record PostDetailsResult(
    CommunityPost post,
    List<Comment> comments
) {}

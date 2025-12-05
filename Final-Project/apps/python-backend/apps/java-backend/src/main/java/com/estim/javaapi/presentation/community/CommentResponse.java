package com.estim.javaapi.presentation.community;

import java.time.Instant;

public record CommentResponse(
    String id,
    String postId,
    String authorId,
    String body,
    String status,
    Instant createdAt,
    Instant updatedAt
) {}

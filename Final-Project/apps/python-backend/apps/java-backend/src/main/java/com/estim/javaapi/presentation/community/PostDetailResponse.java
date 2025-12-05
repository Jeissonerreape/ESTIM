package com.estim.javaapi.presentation.community;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record PostDetailResponse(
    String id,
    String authorId,
    String title,
    String body,
    String type,
    String status,
    boolean pinned,
    Set<String> tags,
    String gameId,
    Instant createdAt,
    Instant updatedAt,
    List<CommentResponse> comments
) {}

package com.estim.javaapi.presentation.community;

import java.time.Instant;
import java.util.Set;

public record PostSummaryResponse(
    String id,
    String authorId,
    String title,
    String type,
    String status,
    boolean pinned,
    Set<String> tags,
    String gameId,
    Instant createdAt,
    Instant updatedAt
) {}

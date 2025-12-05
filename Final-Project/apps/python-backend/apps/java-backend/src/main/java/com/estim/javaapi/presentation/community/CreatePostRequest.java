package com.estim.javaapi.presentation.community;

import java.util.Set;

public record CreatePostRequest(
    String title,
    String body,
    String type,          // "BLOG", "FORUM", "WORKSHOP"
    String gameId,        // optional UUID as string
    Set<String> tags,     // optional
    boolean publishNow,
    boolean pinned
) {}

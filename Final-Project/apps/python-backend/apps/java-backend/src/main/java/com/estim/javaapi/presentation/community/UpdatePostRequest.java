package com.estim.javaapi.presentation.community;

import java.util.Set;

public record UpdatePostRequest(
    String title,         // optional
    String body,          // optional
    Set<String> tags,     // optional
    Boolean pinned,       // optional
    String status,        // optional: "DRAFT", "PUBLISHED", "DELETED"
    String gameId         // optional
) {}

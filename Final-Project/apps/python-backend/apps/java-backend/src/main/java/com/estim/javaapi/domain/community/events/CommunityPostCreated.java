package com.estim.javaapi.domain.community.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

public final class CommunityPostCreated implements DomainEvent {

    private final CommunityPostId postId;
    private final UserId authorId;
    private final String title;
    private final PostType type;
    private final Instant occurredAt;

    public CommunityPostCreated(
        CommunityPostId postId,
        UserId authorId,
        String title,
        PostType type,
        Instant occurredAt
    ) {
        this.postId = postId;
        this.authorId = authorId;
        this.title = title;
        this.type = type;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public CommunityPostId postId() {
        return postId;
    }

    public UserId authorId() {
        return authorId;
    }

    public String title() {
        return title;
    }

    public PostType type() {
        return type;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

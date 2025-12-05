package com.estim.javaapi.domain.community.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

public final class CommentAdded implements DomainEvent {

    private final CommentId commentId;
    private final CommunityPostId postId;
    private final UserId authorId;
    private final Instant occurredAt;

    public CommentAdded(
        CommentId commentId,
        CommunityPostId postId,
        UserId authorId,
        Instant occurredAt
    ) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
        // Same pattern as UserRegistered
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public CommentId commentId() {
        return commentId;
    }

    public CommunityPostId postId() {
        return postId;
    }

    public UserId authorId() {
        return authorId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}

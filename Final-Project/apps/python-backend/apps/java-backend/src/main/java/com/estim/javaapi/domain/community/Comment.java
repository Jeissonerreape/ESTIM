package com.estim.javaapi.domain.community;

import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;
import java.util.Objects;

public class Comment {

    private final CommentId id;
    private final CommunityPostId postId;
    private final UserId authorId;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;
    private CommentStatus status;

    public Comment(
        CommentId id,
        CommunityPostId postId,
        UserId authorId,
        String body,
        Instant createdAt,
        Instant updatedAt,
        CommentStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.postId = Objects.requireNonNull(postId, "postId must not be null");
        this.authorId = Objects.requireNonNull(authorId, "authorId must not be null");
        this.body = Objects.requireNonNull(body, "body must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public static Comment createNew(
        CommunityPostId postId,
        UserId authorId,
        String body,
        Instant now
    ) {
        CommentId id = CommentId.newId();
        Instant timestamp = now != null ? now : Instant.now();
        return new Comment(
            id,
            postId,
            authorId,
            body,
            timestamp,
            timestamp,
            CommentStatus.ACTIVE
        );
    }

    // --- Behaviors ---

    public void edit(String newBody, Instant now) {
        if (newBody == null || newBody.isBlank()) {
            return;
        }
        this.body = newBody;
        touch(now);
    }

    public void markDeleted(Instant now) {
        this.status = CommentStatus.DELETED;
        touch(now);
    }

    private void touch(Instant now) {
        this.updatedAt = (now != null ? now : Instant.now());
    }

    // --- Getters ---

    public CommentId getId() {
        return id;
    }

    public CommunityPostId getPostId() {
        return postId;
    }

    public UserId getAuthorId() {
        return authorId;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public CommentStatus getStatus() {
        return status;
    }
}

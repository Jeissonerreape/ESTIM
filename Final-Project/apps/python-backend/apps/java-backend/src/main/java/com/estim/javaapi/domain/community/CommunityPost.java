package com.estim.javaapi.domain.community;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CommunityPost {

    private final CommunityPostId id;
    private final UserId authorId;
    private String title;
    private String body;
    private PostType type;
    private PostStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean pinned;
    private GameId gameId; // optional, can be null
    private final Set<String> tags = new HashSet<>();

    public CommunityPost(
        CommunityPostId id,
        UserId authorId,
        String title,
        String body,
        PostType type,
        PostStatus status,
        Instant createdAt,
        Instant updatedAt,
        boolean pinned,
        GameId gameId,
        Set<String> tags
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.authorId = Objects.requireNonNull(authorId, "authorId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.body = Objects.requireNonNull(body, "body must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.pinned = pinned;
        this.gameId = gameId;

        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    public static CommunityPost createNew(
        UserId authorId,
        String title,
        String body,
        PostType type,
        GameId gameId,
        Set<String> tags,
        Instant now
    ) {
        CommunityPostId id = CommunityPostId.newId();
        Instant timestamp = now != null ? now : Instant.now();
        return new CommunityPost(
            id,
            authorId,
            title,
            body,
            type,
            PostStatus.DRAFT,
            timestamp,
            timestamp,
            false,
            gameId,
            tags
        );
    }

    // --- Behaviors ---

    public void updateContent(String newTitle, String newBody, Set<String> newTags, Instant now) {
        if (newTitle != null && !newTitle.isBlank()) {
            this.title = newTitle;
        }
        if (newBody != null && !newBody.isBlank()) {
            this.body = newBody;
        }

        if (newTags != null) {
            this.tags.clear();
            this.tags.addAll(newTags);
        }

        touch(now);
    }

    public void publish(Instant now) {
        this.status = PostStatus.PUBLISHED;
        touch(now);
    }

    public void markDeleted(Instant now) {
        this.status = PostStatus.DELETED;
        touch(now);
    }

    public void pin(Instant now) {
        this.pinned = true;
        touch(now);
    }

    public void unpin(Instant now) {
        this.pinned = false;
        touch(now);
    }

    private void touch(Instant now) {
        this.updatedAt = (now != null ? now : Instant.now());
    }

    // --- Getters ---

    public CommunityPostId getId() {
        return id;
    }

    public UserId getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public PostType getType() {
        return type;
    }

    public PostStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPinned() {
        return pinned;
    }

    public GameId getGameId() {
        return gameId;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void setGameId(GameId gameId, Instant now) {
        this.gameId = gameId;
        touch(now);
    }
}

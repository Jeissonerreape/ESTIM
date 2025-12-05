package com.estim.javaapi.infrastructure.persistence.community;

import com.estim.javaapi.domain.community.CommunityPost;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommunityPostMapper {

    public CommunityPost toDomain(CommunityPostJpaEntity entity) {
        if (entity == null) return null;

        return new CommunityPost(
            new CommunityPostId(entity.getId()),
            new UserId(entity.getAuthorId()),
            entity.getTitle(),
            entity.getBody(),
            entity.getType(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.isPinned(),
            entity.getGameId() != null ? new GameId(entity.getGameId()) : null,
            entity.getTags()
        );
    }

    public CommunityPostJpaEntity toEntity(CommunityPost post) {
        CommunityPostJpaEntity entity = new CommunityPostJpaEntity();

        entity.setId(post.getId().value());
        entity.setAuthorId(post.getAuthorId().value());
        entity.setTitle(post.getTitle());
        entity.setBody(post.getBody());
        entity.setType(post.getType());
        entity.setStatus(post.getStatus());
        entity.setCreatedAt(post.getCreatedAt());
        entity.setUpdatedAt(post.getUpdatedAt());
        entity.setPinned(post.isPinned());
        entity.setGameId(post.getGameId() != null ? post.getGameId().getValue() : null);
        entity.setTags(post.getTags());

        return entity;
    }
}

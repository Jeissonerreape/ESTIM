package com.estim.javaapi.infrastructure.persistence.community;

import com.estim.javaapi.domain.community.Comment;
import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toDomain(CommentJpaEntity entity) {
        if (entity == null) return null;

        return new Comment(
            new CommentId(entity.getId()),
            new CommunityPostId(entity.getPostId()),
            new UserId(entity.getAuthorId()),
            entity.getBody(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getStatus()
        );
    }

    public CommentJpaEntity toEntity(Comment comment) {
        CommentJpaEntity entity = new CommentJpaEntity();

        entity.setId(comment.getId().value());
        entity.setPostId(comment.getPostId().value());
        entity.setAuthorId(comment.getAuthorId().value());
        entity.setBody(comment.getBody());
        entity.setStatus(comment.getStatus());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setUpdatedAt(comment.getUpdatedAt());

        return entity;
    }
}

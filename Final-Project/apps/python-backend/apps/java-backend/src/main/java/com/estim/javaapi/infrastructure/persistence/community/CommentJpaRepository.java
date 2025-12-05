package com.estim.javaapi.infrastructure.persistence.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, UUID> {

    List<CommentJpaEntity> findByPostId(UUID postId);

    List<CommentJpaEntity> findByAuthorId(UUID authorId);
}

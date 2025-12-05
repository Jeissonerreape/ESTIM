package com.estim.javaapi.domain.community;

import com.estim.javaapi.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Optional<Comment> findById(CommentId id);

    List<Comment> findByPostId(CommunityPostId postId);

    List<Comment> findByAuthor(UserId authorId);

    void save(Comment comment);

    void delete(CommentId id);
}

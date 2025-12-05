package com.estim.javaapi.infrastructure.persistence.community;

import com.estim.javaapi.domain.community.Comment;
import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.community.CommentRepository;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository jpaRepository;
    private final CommentMapper mapper;

    public CommentRepositoryImpl(CommentJpaRepository jpaRepository,
                                 CommentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Comment> findById(CommentId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Comment> findByPostId(CommunityPostId postId) {
        return jpaRepository.findByPostId(postId.value()).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Comment> findByAuthor(UserId authorId) {
        return jpaRepository.findByAuthorId(authorId.value()).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void save(Comment comment) {
        jpaRepository.save(mapper.toEntity(comment));
    }

    @Override
    public void delete(CommentId id) {
        jpaRepository.deleteById(id.value());
    }
}

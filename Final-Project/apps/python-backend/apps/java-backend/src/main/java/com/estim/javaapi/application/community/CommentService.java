package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.community.Comment;
import com.estim.javaapi.domain.community.CommentRepository;
import com.estim.javaapi.domain.community.CommunityPost;
import com.estim.javaapi.domain.community.CommunityPostRepository;
import com.estim.javaapi.domain.community.events.CommentAdded;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class CommentService {

    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final DomainEventPublisher eventPublisher;

    public CommentService(CommunityPostRepository postRepository,
                          CommentRepository commentRepository,
                          DomainEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Comment addComment(AddCommentCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        CommunityPost post = postRepository.findById(command.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + command.postId()));

        if (post.getStatus() == null || post.getStatus().name().equals("DELETED")) {
            throw new IllegalStateException("Cannot comment on deleted post");
        }

        Instant now = Instant.now();

        Comment comment = Comment.createNew(
            post.getId(),
            command.authorId(),
            command.body(),
            now
        );

        commentRepository.save(comment);

        eventPublisher.publish(new CommentAdded(
            comment.getId(),
            comment.getPostId(),
            comment.getAuthorId(),
            now
        ));

        return comment;
    }

    @Transactional
    public Comment updateComment(UpdateCommentCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Comment comment = commentRepository.findById(command.commentId())
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + command.commentId()));

        comment.edit(command.body(), Instant.now());
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    public void deleteComment(DeleteCommentCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Comment comment = commentRepository.findById(command.commentId())
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + command.commentId()));

        comment.markDeleted(Instant.now());
        commentRepository.save(comment);

        // If at some point you want hard delete:
        // commentRepository.delete(command.commentId());
    }
}

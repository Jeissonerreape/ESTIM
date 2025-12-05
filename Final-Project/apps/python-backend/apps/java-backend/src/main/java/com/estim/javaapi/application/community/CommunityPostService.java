package com.estim.javaapi.application.community;

import com.estim.javaapi.domain.common.DomainEventPublisher;
import com.estim.javaapi.domain.community.CommentRepository;
import com.estim.javaapi.domain.community.CommunityPost;
import com.estim.javaapi.domain.community.CommunityPostRepository;
import com.estim.javaapi.domain.community.PostStatus;
import com.estim.javaapi.domain.community.events.CommunityPostCreated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Locale;

@Service
public class CommunityPostService {

    private final CommunityPostRepository postRepository;
    private final CommentRepository commentRepository;
    private final DomainEventPublisher eventPublisher;

    public CommunityPostService(CommunityPostRepository postRepository,
                                CommentRepository commentRepository,
                                DomainEventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CommunityPost createPost(CreatePostCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        Instant now = Instant.now();

        CommunityPost post = CommunityPost.createNew(
            command.authorId(),
            command.title(),
            command.body(),
            command.type(),
            command.gameId(),
            command.tags(),
            now
        );

        if (command.pinned()) {
            post.pin(now);
        }

        if (command.publishNow()) {
            post.publish(now);
        }

        postRepository.save(post);

        // Domain event – can be consumed by ActivityFeedService, etc.
        eventPublisher.publish(new CommunityPostCreated(
            post.getId(),
            post.getAuthorId(),
            post.getTitle(),
            post.getType(),
            now
        ));

        return post;
    }

    @Transactional
    public CommunityPost updatePost(UpdatePostCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        CommunityPost post = postRepository.findById(command.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + command.postId()));

        Instant now = Instant.now();

        // title/body/tags
        post.updateContent(
            command.title() != null ? command.title() : post.getTitle(),
            command.body() != null ? command.body() : post.getBody(),
            command.tags() != null ? command.tags() : post.getTags(),
            now
        );

        // gameId
        if (command.gameId() != null) {
            post.setGameId(command.gameId(), now);
        }

        // pinned
        if (Boolean.TRUE.equals(command.pinned())) {
            post.pin(now);
        } else if (Boolean.FALSE.equals(command.pinned())) {
            post.unpin(now);
        }

        // status (publish / unpublish / delete)
        if (command.status() != null) {
            PostStatus status = command.status();
            switch (status) {
                case PUBLISHED -> post.publish(now);
                case DRAFT -> {
                    // TODO: add explicit "setDraft" if you want special behavior
                }
                case DELETED -> post.markDeleted(now);
            }
        }

        postRepository.save(post);
        return post;
    }

    @Transactional
    public void deletePost(DeletePostCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // For now: soft delete through aggregate, not physical delete
        CommunityPost post = postRepository.findById(command.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + command.postId()));

        post.markDeleted(Instant.now());
        postRepository.save(post);

        // If you ever want hard delete instead:
        // postRepository.delete(command.postId());
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> listPosts(ListPostsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        boolean hasFilters =
            query.type() != null ||
                query.gameId() != null ||
                (query.searchText() != null && !query.searchText().isBlank()) ||
                (query.tag() != null && !query.tag().isBlank());

        List<CommunityPost> base;

        // 1) No filters at all → just list all posts, paginated
        if (!hasFilters) {
            base = postRepository.findAll(query.limit(), query.offset());
        }
        // 2) searchText has priority if present
        else if (query.searchText() != null && !query.searchText().isBlank()) {
            base = postRepository.search(query.searchText(), query.limit(), query.offset());
        }
        // 3) filter by game
        else if (query.gameId() != null) {
            base = postRepository.findByGame(query.gameId(), query.limit(), query.offset());
        }
        // 4) filter by type
        else if (query.type() != null) {
            base = postRepository.findByType(query.type(), query.limit(), query.offset());
        }
        // 5) Fallback (e.g. only tag was set)
        else {
            base = postRepository.findAll(query.limit(), query.offset());
        }

        // 🔎 Now apply tag filtering in-memory if needed
        if (query.tag() != null && !query.tag().isBlank()) {
            String tagLower = query.tag().toLowerCase(Locale.ROOT);

            base = base.stream()
                .filter(p -> p.getTags() != null &&
                    p.getTags().stream()
                        .anyMatch(t -> t != null &&
                            t.toLowerCase(Locale.ROOT).equals(tagLower)))
                .toList();
        }

        return base;
    }


    @Transactional(readOnly = true)
    public PostDetailsResult getPostDetails(GetPostDetailsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        CommunityPost post = postRepository.findById(query.postId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + query.postId()));

        var comments = commentRepository.findByPostId(post.getId());

        return new PostDetailsResult(post, comments);
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> listUserPosts(ListUserPostsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var posts = postRepository.findByAuthor(query.authorId());

        if (query.type() == null) {
            return posts;
        }

        // Simple filter by type in memory; you can push this down to the repository later
        return posts.stream()
            .filter(p -> p.getType() == query.type())
            .skip(query.offset())
            .limit(query.limit())
            .toList();
    }
}

package com.estim.javaapi.controllers;

import com.estim.javaapi.application.community.*;
import com.estim.javaapi.domain.community.Comment;
import com.estim.javaapi.domain.community.CommentStatus;
import com.estim.javaapi.domain.community.CommentId;
import com.estim.javaapi.domain.community.CommunityPost;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.community.PostStatus;
import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.infrastructure.security.AuthenticatedUser;
import com.estim.javaapi.presentation.community.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
public class CommunityController {

    private final CommunityPostService communityPostService;
    private final CommentService commentService;

    public CommunityController(CommunityPostService communityPostService,
                               CommentService commentService) {
        this.communityPostService = communityPostService;
        this.commentService = commentService;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    // NOTE: we no longer use the custom thread-local SecurityContext here.
    // All HTTP auth comes from Spring Security + JwtAuthenticationFilter.

    private static PostType parsePostType(String raw) {
        if (raw == null) return null;
        return PostType.valueOf(raw.toUpperCase(Locale.ROOT));
    }

    private static PostStatus parsePostStatus(String raw) {
        if (raw == null) return null;
        return PostStatus.valueOf(raw.toUpperCase(Locale.ROOT));
    }

    private static GameId parseGameId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return new GameId(UUID.fromString(raw));
    }

    private static PostSummaryResponse toSummaryResponse(CommunityPost post) {
        return new PostSummaryResponse(
            post.getId().toString(),
            post.getAuthorId().value().toString(),
            post.getTitle(),
            post.getType().name(),
            post.getStatus().name(),
            post.isPinned(),
            post.getTags(),
            post.getGameId() != null ? post.getGameId().getValue().toString() : null,
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }

    private static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
            comment.getId().toString(),
            comment.getPostId().toString(),
            comment.getAuthorId().value().toString(),
            comment.getBody(),
            comment.getStatus().name(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }

    private static PostDetailResponse toDetailResponse(PostDetailsResult details) {
        CommunityPost post = details.post();
        List<CommentResponse> commentResponses = details.comments().stream()
            .filter(c -> c.getStatus() != CommentStatus.DELETED) // hide deleted, if you want
            .map(CommunityController::toCommentResponse)
            .toList();

        return new PostDetailResponse(
            post.getId().toString(),
            post.getAuthorId().value().toString(),
            post.getTitle(),
            post.getBody(),
            post.getType().name(),
            post.getStatus().name(),
            post.isPinned(),
            post.getTags(),
            post.getGameId() != null ? post.getGameId().getValue().toString() : null,
            post.getCreatedAt(),
            post.getUpdatedAt(),
            commentResponses
        );
    }

    // -------------------------------------------------------------------------
    // GET /community/posts   (PUBLIC)
    // -------------------------------------------------------------------------

    @GetMapping("/posts")
    public List<PostSummaryResponse> listPosts(
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "gameId", required = false) String gameId,
        @RequestParam(name = "tag", required = false) String tag,
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "onlyPublished", defaultValue = "true") boolean onlyPublished,
        @RequestParam(name = "limit", defaultValue = "20") int limit,
        @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        PostType postType = parsePostType(type);
        GameId game = parseGameId(gameId);

        ListPostsQuery query = new ListPostsQuery(
            postType,
            game,
            tag,
            search,
            onlyPublished,
            limit,
            offset
        );

        List<CommunityPost> posts = communityPostService.listPosts(query);

        // If onlyPublished is true and repo doesn't enforce it, we can filter here:
        if (onlyPublished) {
            posts = posts.stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED)
                .collect(Collectors.toList());
        }

        return posts.stream()
            .map(CommunityController::toSummaryResponse)
            .toList();
    }

    // -------------------------------------------------------------------------
    // GET /community/posts/{postId}   (PUBLIC)
    // -------------------------------------------------------------------------

    @GetMapping("/posts/{postId}")
    public PostDetailResponse getPost(@PathVariable String postId) {
        CommunityPostId id = new CommunityPostId(UUID.fromString(postId));

        PostDetailsResult details = communityPostService.getPostDetails(
            new GetPostDetailsQuery(id)
        );

        return toDetailResponse(details);
    }

    // -------------------------------------------------------------------------
    // POST /community/posts   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDetailResponse createPost(
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @RequestBody CreatePostRequest request
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId authorId = currentUser.userId();

        PostType type = parsePostType(request.type());
        if (type == null) {
            throw new IllegalArgumentException("Post type is required");
        }

        GameId gameId = parseGameId(request.gameId());
        Set<String> tags = request.tags();

        CreatePostCommand command = new CreatePostCommand(
            authorId,
            request.title(),
            request.body(),
            type,
            gameId,
            tags,
            request.publishNow(),
            request.pinned()
        );

        CommunityPost created = communityPostService.createPost(command);

        // Reuse the details query to include comments (initially empty)
        PostDetailsResult details = communityPostService.getPostDetails(
            new GetPostDetailsQuery(created.getId())
        );

        return toDetailResponse(details);
    }

    // -------------------------------------------------------------------------
    // PATCH /community/posts/{postId}   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @PatchMapping("/posts/{postId}")
    public PostDetailResponse updatePost(
        @PathVariable String postId,
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @RequestBody UpdatePostRequest request
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId editorId = currentUser.userId();
        CommunityPostId id = new CommunityPostId(UUID.fromString(postId));

        PostStatus status = parsePostStatus(request.status());
        GameId gameId = parseGameId(request.gameId());

        UpdatePostCommand command = new UpdatePostCommand(
            id,
            editorId,
            request.title(),
            request.body(),
            request.tags(),
            request.pinned(),
            status,
            gameId
        );

        CommunityPost updated = communityPostService.updatePost(command);

        PostDetailsResult details = communityPostService.getPostDetails(
            new GetPostDetailsQuery(updated.getId())
        );

        return toDetailResponse(details);
    }

    // -------------------------------------------------------------------------
    // DELETE /community/posts/{postId}   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
        @PathVariable String postId,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId requesterId = currentUser.userId();
        CommunityPostId id = new CommunityPostId(UUID.fromString(postId));

        DeletePostCommand command = new DeletePostCommand(id, requesterId);
        communityPostService.deletePost(command);
    }

    // -------------------------------------------------------------------------
    // POST /community/posts/{postId}/comments   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
        @PathVariable String postId,
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @RequestBody CommentRequest request
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId authorId = currentUser.userId();
        CommunityPostId communityPostId = new CommunityPostId(UUID.fromString(postId));

        AddCommentCommand command = new AddCommentCommand(
            communityPostId,
            authorId,
            request.body()
        );

        Comment comment = commentService.addComment(command);
        return toCommentResponse(comment);
    }

    // -------------------------------------------------------------------------
    // PATCH /community/comments/{commentId}   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @PatchMapping("/comments/{commentId}")
    public CommentResponse updateComment(
        @PathVariable String commentId,
        @AuthenticationPrincipal AuthenticatedUser currentUser,
        @RequestBody CommentRequest request
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId editorId = currentUser.userId();
        CommentId id = new CommentId(UUID.fromString(commentId));

        UpdateCommentCommand command = new UpdateCommentCommand(
            id,
            editorId,
            request.body()
        );

        Comment updated = commentService.updateComment(command);
        return toCommentResponse(updated);
    }

    // -------------------------------------------------------------------------
    // DELETE /community/comments/{commentId}   (AUTH REQUIRED)
    // -------------------------------------------------------------------------

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
        @PathVariable String commentId,
        @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        UserId requesterId = currentUser.userId();
        CommentId id = new CommentId(UUID.fromString(commentId));

        DeleteCommentCommand command = new DeleteCommentCommand(id, requesterId);
        commentService.deleteComment(command);
    }
}

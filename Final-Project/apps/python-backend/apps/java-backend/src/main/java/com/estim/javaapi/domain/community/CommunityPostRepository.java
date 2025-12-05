package com.estim.javaapi.domain.community;

import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository {

    Optional<CommunityPost> findById(CommunityPostId id);

    List<CommunityPost> findByAuthor(UserId authorId);

    List<CommunityPost> findByType(PostType type, int limit, int offset);

    List<CommunityPost> findByGame(GameId gameId, int limit, int offset);

    List<CommunityPost> findAll(int limit, int offset);

    /**
     * Simple search; can be implemented with LIKE / full-text / etc.
     */
    List<CommunityPost> search(String searchText, int limit, int offset);

    void save(CommunityPost post);

    void delete(CommunityPostId id);
}

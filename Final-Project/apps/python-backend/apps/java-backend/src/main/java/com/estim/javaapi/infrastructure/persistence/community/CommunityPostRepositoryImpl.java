package com.estim.javaapi.infrastructure.persistence.community;

import com.estim.javaapi.domain.community.CommunityPost;
import com.estim.javaapi.domain.community.CommunityPostId;
import com.estim.javaapi.domain.community.CommunityPostRepository;
import com.estim.javaapi.domain.community.PostType;
import com.estim.javaapi.domain.library.GameId;
import com.estim.javaapi.domain.user.UserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommunityPostRepositoryImpl implements CommunityPostRepository {

    private final CommunityPostJpaRepository jpaRepository;
    private final CommunityPostMapper mapper;

    public CommunityPostRepositoryImpl(CommunityPostJpaRepository jpaRepository,
                                       CommunityPostMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CommunityPost> findById(CommunityPostId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<CommunityPost> findByAuthor(UserId authorId) {
        return jpaRepository.findByAuthorId(authorId.value()).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<CommunityPost> findByType(PostType type, int limit, int offset) {
        var page = jpaRepository.findByType(type, PageRequest.of(offset / limit, limit));
        return page.stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<CommunityPost> findByGame(GameId gameId, int limit, int offset) {
        var page = jpaRepository.findByGameId(gameId.getValue(), PageRequest.of(offset / limit, limit));
        return page.stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<CommunityPost> search(String searchText, int limit, int offset) {
        var page = jpaRepository.search(searchText, PageRequest.of(offset / limit, limit));
        return page.stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<CommunityPost> findAll(int limit, int offset) {
        var page = jpaRepository.findAll(PageRequest.of(offset / limit, limit));
        return page.stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void save(CommunityPost post) {
        jpaRepository.save(mapper.toEntity(post));
    }

    @Override
    public void delete(CommunityPostId id) {
        jpaRepository.deleteById(id.value());
    }
}

package com.estim.javaapi.infrastructure.persistence.community;

import com.estim.javaapi.domain.community.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommunityPostJpaRepository extends JpaRepository<CommunityPostJpaEntity, UUID> {

    List<CommunityPostJpaEntity> findByAuthorId(UUID authorId);

    List<CommunityPostJpaEntity> findByType(PostType type, Pageable pageable);

    List<CommunityPostJpaEntity> findByGameId(UUID gameId, Pageable pageable);

    @Query("""
           select p from CommunityPostJpaEntity p
           where (:searchText is null
                  or lower(p.title) like lower(concat('%', :searchText, '%'))
                  or lower(p.body) like lower(concat('%', :searchText, '%')))
           """)
    List<CommunityPostJpaEntity> search(String searchText, Pageable pageable);
}

package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningUserProgressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningUserProgressRepository extends JpaRepository<LearningUserProgressEntity, String> {

    Optional<LearningUserProgressEntity> findByUserIdAndTrackId(String userId, String trackId);

    @Query("SELECT p FROM LearningUserProgressEntity p WHERE " +
            "(:trackId IS NULL OR p.trackId = :trackId) AND " +
            "(:query IS NULL OR LOWER(p.userId) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<LearningUserProgressEntity> findProgressFiltered(
            @Param("trackId") String trackId,
            @Param("query") String query,
            Pageable pageable
    );
}

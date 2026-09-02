package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningLevelRepository extends JpaRepository<LearningLevelEntity, String> {

    List<LearningLevelEntity> findAllByTrackIdOrderBySortOrderAsc(String trackId);

    List<LearningLevelEntity> findAllByTrackIdAndStatusOrderBySortOrderAsc(String trackId, LearningStatus status);

    Optional<LearningLevelEntity> findByTrackIdAndLevelNumber(String trackId, int levelNumber);

    @Query("SELECT l FROM LearningLevelEntity l WHERE " +
            "(:trackId IS NULL OR l.trackId = :trackId) AND " +
            "(:status IS NULL OR l.status = :status) AND " +
            "(:query IS NULL OR LOWER(l.id) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(CAST(l.translationsJson AS string)) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<LearningLevelEntity> findLevelsFiltered(
            @Param("trackId") String trackId,
            @Param("status") LearningStatus status,
            @Param("query") String query,
            Pageable pageable
    );

    long countByTrackId(String trackId);
    long countByTrackIdAndStatus(String trackId, LearningStatus status);
}

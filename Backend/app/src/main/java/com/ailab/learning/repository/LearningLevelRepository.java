package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningLevelEntity;
import com.ailab.learning.domain.LearningStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningLevelRepository extends JpaRepository<LearningLevelEntity, String>, JpaSpecificationExecutor<LearningLevelEntity> {

    List<LearningLevelEntity> findAllByTrackIdOrderBySortOrderAsc(String trackId);

    List<LearningLevelEntity> findAllByTrackIdAndStatusOrderBySortOrderAsc(String trackId, LearningStatus status);

    Optional<LearningLevelEntity> findByTrackIdAndLevelNumber(String trackId, int levelNumber);

    long countByTrackId(String trackId);
    long countByTrackIdAndStatus(String trackId, LearningStatus status);
}

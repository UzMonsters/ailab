package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningLevelPublishedSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningLevelPublishedSnapshotRepository extends JpaRepository<LearningLevelPublishedSnapshotEntity, String> {

    Optional<LearningLevelPublishedSnapshotEntity> findByLevelIdAndVersion(String levelId, long version);

    Optional<LearningLevelPublishedSnapshotEntity> findFirstByLevelIdOrderByVersionDesc(String levelId);

    Optional<LearningLevelPublishedSnapshotEntity> findByIdempotencyKey(String idempotencyKey);

    List<LearningLevelPublishedSnapshotEntity> findAllByLevelIdOrderByVersionDesc(String levelId);
}

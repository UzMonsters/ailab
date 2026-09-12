package com.ailab.learning.repository;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningUserAttemptRepository extends JpaRepository<LearningUserAttemptEntity, String>, JpaSpecificationExecutor<LearningUserAttemptEntity> {

    Optional<LearningUserAttemptEntity> findByClientAttemptId(String clientAttemptId);

    Optional<LearningUserAttemptEntity> findFirstByUserIdAndLevelIdAndStatusOrderByStartedAtDesc(
            String userId, String levelId, AttemptStatus status);

    Optional<LearningUserAttemptEntity> findByIdempotencyKey(String idempotencyKey);

    List<LearningUserAttemptEntity> findAllByUserIdAndLevelIdOrderByStartedAtDesc(String userId, String levelId);

    List<LearningUserAttemptEntity> findAllByLevelId(String levelId);

    long countByLevelId(String levelId);
    long countByLevelIdAndStatus(String levelId, AttemptStatus status);

    long countByStatus(AttemptStatus status);
    long countByUserId(String userId);
    long countByUserIdAndStatus(String userId, AttemptStatus status);
    List<LearningUserAttemptEntity> findTop20ByUserIdOrderByStartedAtDesc(String userId);
}

package com.ailab.learning.repository;

import com.ailab.learning.domain.AttemptStatus;
import com.ailab.learning.domain.LearningUserAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningUserAttemptRepository extends JpaRepository<LearningUserAttemptEntity, String> {

    Optional<LearningUserAttemptEntity> findByClientAttemptId(String clientAttemptId);

    Optional<LearningUserAttemptEntity> findFirstByUserIdAndLevelIdAndStatusOrderByStartedAtDesc(
            String userId, String levelId, AttemptStatus status);

    Optional<LearningUserAttemptEntity> findByIdempotencyKey(String idempotencyKey);

    List<LearningUserAttemptEntity> findAllByUserIdAndLevelIdOrderByStartedAtDesc(String userId, String levelId);

    List<LearningUserAttemptEntity> findAllByLevelId(String levelId);

    @Query("SELECT a FROM LearningUserAttemptEntity a WHERE " +
            "(:levelId IS NULL OR a.levelId = :levelId) AND " +
            "(:from IS NULL OR a.startedAt >= :from) AND " +
            "(:to IS NULL OR a.startedAt <= :to)")
    List<LearningUserAttemptEntity> findForAnalytics(
            @Param("levelId") String levelId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    long countByLevelId(String levelId);
    long countByLevelIdAndStatus(String levelId, AttemptStatus status);
}

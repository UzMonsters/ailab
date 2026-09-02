package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningProgressResetAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningProgressResetAuditRepository extends JpaRepository<LearningProgressResetAuditEntity, String> {

    List<LearningProgressResetAuditEntity> findAllByUserIdOrderByCreatedAtDesc(String userId);
}

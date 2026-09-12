package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningUserProgressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningUserProgressRepository extends JpaRepository<LearningUserProgressEntity, String>, JpaSpecificationExecutor<LearningUserProgressEntity> {

    Optional<LearningUserProgressEntity> findByUserIdAndTrackId(String userId, String trackId);

    java.util.List<LearningUserProgressEntity> findAllByUserId(String userId);
}

package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningStatus;
import com.ailab.learning.domain.LearningTrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningTrackRepository extends JpaRepository<LearningTrackEntity, String> {

    Optional<LearningTrackEntity> findByCode(String code);

    List<LearningTrackEntity> findAllByOrderBySortOrderAsc();

    Page<LearningTrackEntity> findAllByStatusOrderBySortOrderAsc(LearningStatus status, Pageable pageable);
}

package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningChapterRepository extends JpaRepository<LearningChapterEntity, String> {

    List<LearningChapterEntity> findAllByTrackIdOrderBySortOrderAsc(String trackId);
}

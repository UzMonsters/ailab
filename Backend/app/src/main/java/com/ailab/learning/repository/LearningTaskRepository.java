package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningTaskRepository extends JpaRepository<LearningTaskEntity, String> {

    Optional<LearningTaskEntity> findByCode(String code);
}

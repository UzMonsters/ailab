package com.ailab.learning.repository;

import com.ailab.learning.domain.LearningRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LearningRewardRepository extends JpaRepository<LearningRewardEntity, String> {

    Optional<LearningRewardEntity> findByCode(String code);
}

package com.ailab.chemistry.infrastructure.persistence.classification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaClassificationProfileRepository extends JpaRepository<ClassificationProfileEntity, UUID> {
    Optional<ClassificationProfileEntity> findByCompoundId(UUID compoundId);

    @Query("SELECT p FROM ClassificationProfileEntity p WHERE p.compound.compoundCode = :compoundCode")
    Optional<ClassificationProfileEntity> findByCompoundCode(@Param("compoundCode") String compoundCode);

    @Query("SELECT DISTINCT p FROM ClassificationProfileEntity p JOIN p.assignments a WHERE a.code = :code")
    List<ClassificationProfileEntity> findByAssignmentCode(@Param("code") String code);
}

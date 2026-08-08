package com.ailab.chemistry.infrastructure.persistence.hazard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaHazardProfileRepository extends JpaRepository<HazardProfileEntity, UUID> {
    Optional<HazardProfileEntity> findByCompoundId(UUID compoundId);

    @Query("SELECT p FROM HazardProfileEntity p WHERE p.compound.compoundCode = :compoundCode")
    Optional<HazardProfileEntity> findByCompoundCode(@Param("compoundCode") String compoundCode);
}

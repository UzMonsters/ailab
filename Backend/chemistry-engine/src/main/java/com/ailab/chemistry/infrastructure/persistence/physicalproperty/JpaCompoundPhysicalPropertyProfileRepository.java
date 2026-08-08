package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCompoundPhysicalPropertyProfileRepository extends JpaRepository<CompoundPhysicalPropertyProfileEntity, UUID> {
    Optional<CompoundPhysicalPropertyProfileEntity> findByCompoundId(UUID compoundId);

    @Query("SELECT p FROM CompoundPhysicalPropertyProfileEntity p WHERE p.compound.compoundCode = :compoundCode")
    Optional<CompoundPhysicalPropertyProfileEntity> findByCompoundCode(@Param("compoundCode") String compoundCode);

    @Query("SELECT DISTINCT p FROM CompoundPhysicalPropertyProfileEntity p JOIN PropertyAvailabilityEntity a ON a.profile.id = p.id WHERE a.propertyType = :propertyType AND a.availabilityStatus = 'AVAILABLE'")
    List<CompoundPhysicalPropertyProfileEntity> findWithAvailableProperty(@Param("propertyType") String propertyType);
}

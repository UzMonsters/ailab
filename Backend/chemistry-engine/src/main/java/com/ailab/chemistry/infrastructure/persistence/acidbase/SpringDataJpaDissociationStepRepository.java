package com.ailab.chemistry.infrastructure.persistence.acidbase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaDissociationStepRepository extends JpaRepository<JpaDissociationStepEntity, UUID> {
    List<JpaDissociationStepEntity> findByAcidSpeciesCodeIgnoreCaseOrderByStepNumberAsc(String acidSpeciesCode);
}

package com.ailab.chemistry.infrastructure.persistence.classification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JpaClassificationDefinitionRepository extends JpaRepository<ClassificationDefinitionEntity, UUID> {
    List<ClassificationDefinitionEntity> findByTaxonomyVersionId(String taxonomyVersionId);
}

package com.ailab.chemistry.infrastructure.persistence.classification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaClassificationTaxonomyVersionRepository extends JpaRepository<ClassificationTaxonomyVersionEntity, String> {
}

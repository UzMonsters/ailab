package com.ailab.chemistry.domain.classification;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.List;
import java.util.Optional;

public interface ClassificationProfileRepository {
    Optional<ClassificationProfile> findByCompoundId(CompoundId compoundId);
    Optional<ClassificationProfile> findByCompoundCode(String compoundCode);
    List<ClassificationProfile> findByClassificationCode(ClassificationCode classificationCode);
    List<ClassificationProfile> findAll();
    long count();
    ClassificationTaxonomy getActiveTaxonomy();
}

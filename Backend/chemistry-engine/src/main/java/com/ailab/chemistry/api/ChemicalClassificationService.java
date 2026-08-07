package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.classification.ClassificationExplanation;

import java.util.List;
import java.util.UUID;

public interface ChemicalClassificationService {

    CompoundClassificationDetails getByCompoundId(UUID compoundId);

    CompoundClassificationDetails getByCompoundCode(String compoundCode);

    List<CompoundSummary> findCompoundsByClassification(String classificationCode);

    ClassificationTaxonomyDetails getActiveTaxonomy();

    ClassificationExplanation explain(String compoundCode, String classificationCode);
}

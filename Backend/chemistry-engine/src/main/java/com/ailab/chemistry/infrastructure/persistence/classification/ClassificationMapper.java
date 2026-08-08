package com.ailab.chemistry.infrastructure.persistence.classification;

import com.ailab.chemistry.domain.classification.*;
import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.ArrayList;
import java.util.List;

public final class ClassificationMapper {

    private ClassificationMapper() {}

    public static ClassificationProfile toDomain(ClassificationProfileEntity entity) {
        if (entity == null) return null;

        List<ClassificationAssignment> assignments = new ArrayList<>();
        if (entity.getAssignments() != null) {
            for (ClassificationAssignmentEntity a : entity.getAssignments()) {
                ClassificationCode code = new ClassificationCode(a.getCode());
                ClassificationDimension dimension = ClassificationDimension.valueOf(a.getDimension());
                ClassificationBasis basis = ClassificationBasis.valueOf(a.getBasis());
                ClassificationEvidenceStatus evidenceStatus = ClassificationEvidenceStatus.valueOf(a.getEvidenceStatus());
                ClassificationRuleCode ruleCode = a.getRuleCode() != null ? ClassificationRuleCode.valueOf(a.getRuleCode().replace("-", "_")) : null;

                ClassificationProvenance provenance = a.getSourceIdentifier() != null ?
                        new ClassificationProvenance(a.getSourceIdentifier(), a.getSourceTitle(), "Publisher", "v1.0.0", "2026-08-05", a.getExplanatoryNote()) :
                        (ruleCode != null ? ClassificationProvenance.derivedRule(ruleCode) : new ClassificationProvenance("INTERNAL", "Internal Rule Engine", "AI Laboratory", "v1.0.0", "2026-08-05", a.getExplanatoryNote()));

                assignments.add(new ClassificationAssignment(code, dimension, basis, evidenceStatus, ruleCode, provenance, a.getExplanatoryNote()));
            }
        }

        ClassificationTaxonomyVersion version = KnownClassificationRegistry.VERSION;
        return new ClassificationProfile(new CompoundId(entity.getCompound().getId()), version, assignments);
    }
}

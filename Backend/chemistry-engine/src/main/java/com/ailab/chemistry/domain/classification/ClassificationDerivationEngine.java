package com.ailab.chemistry.domain.classification;

import com.ailab.chemistry.domain.compound.Compound;

import java.util.ArrayList;
import java.util.List;

public final class ClassificationDerivationEngine {

    private ClassificationDerivationEngine() {}

    public static List<ClassificationAssignment> deriveSafeAssignments(Compound compound) {
        if (compound == null) return List.of();
        List<ClassificationAssignment> derived = new ArrayList<>();

        int distinctElementCount = compound.getComposition().getElementCounts().size();

        // 1. Elemental substance rule
        if (distinctElementCount == 1) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("ELEMENTAL_SUBSTANCE"),
                    ClassificationDimension.SUBSTANCE_DOMAIN,
                    ClassificationRuleCode.RULE_ELEMENTAL_COMPOSITION,
                    "Single element composition derived as ELEMENTAL_SUBSTANCE"
            ));
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("MONOATOMIC_OR_ELEMENTAL"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_ELEMENTAL_COMPOSITION,
                    "Single element composition derived as MONOATOMIC_OR_ELEMENTAL"
            ));
        }

        // 2. Distinct element count rule
        if (distinctElementCount == 2) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("BINARY_COMPOSITION"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_DISTINCT_ELEMENT_COUNT,
                    "Two distinct element types derived as BINARY_COMPOSITION"
            ));
        } else if (distinctElementCount == 3) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("TERNARY_COMPOSITION"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_DISTINCT_ELEMENT_COUNT,
                    "Three distinct element types derived as TERNARY_COMPOSITION"
            ));
        } else if (distinctElementCount >= 4) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("QUATERNARY_OR_HIGHER_COMPOSITION"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_DISTINCT_ELEMENT_COUNT,
                    "Four or more distinct element types derived as QUATERNARY_OR_HIGHER_COMPOSITION"
            ));
        }

        // 3. Hydrate presence rule
        if (compound.getFormula().getHydrateInfo() != null) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("HYDRATE"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_HYDRATE_PRESENCE,
                    "Hydrate notation present derived as HYDRATE"
            ));
        }

        // 4. Net charge rule
        if (compound.getNetCharge().getValue() == 0) {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("NEUTRAL_SPECIES"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_NET_CHARGE,
                    "Net charge 0 derived as NEUTRAL_SPECIES"
            ));
        } else {
            derived.add(ClassificationAssignment.derived(
                    new ClassificationCode("CHARGED_SPECIES"),
                    ClassificationDimension.COMPOSITION_PATTERN,
                    ClassificationRuleCode.RULE_NET_CHARGE,
                    "Non-zero net charge derived as CHARGED_SPECIES"
            ));
        }

        return derived;
    }
}

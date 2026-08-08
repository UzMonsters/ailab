package com.ailab.chemistry.domain.classification;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.*;
import java.util.stream.Collectors;

public final class ClassificationProfile {
    private final CompoundId compoundId;
    private final ClassificationTaxonomyVersion taxonomyVersion;
    private final List<ClassificationAssignment> assignments;

    public ClassificationProfile(CompoundId compoundId, ClassificationTaxonomyVersion taxonomyVersion, List<ClassificationAssignment> assignments) {
        if (compoundId == null) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND, "CompoundId cannot be null for profile");
        }
        if (taxonomyVersion == null) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_TAXONOMY_NOT_FOUND, "TaxonomyVersion cannot be null for profile");
        }
        if (assignments == null || assignments.isEmpty()) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_DATASET_INCOMPLETE, "Profile assignments cannot be empty");
        }

        // Validate duplicates & cardinality
        Set<ClassificationCode> seenCodes = new HashSet<>();
        Map<ClassificationDimension, List<ClassificationAssignment>> byDimension = new EnumMap<>(ClassificationDimension.class);

        for (ClassificationAssignment a : assignments) {
            if (!seenCodes.add(a.getCode())) {
                throw new ClassificationException(ClassificationErrorCode.DUPLICATE_CLASSIFICATION_ASSIGNMENT,
                        "Duplicate assignment code in profile: " + a.getCode());
            }
            byDimension.computeIfAbsent(a.getDimension(), k -> new ArrayList<>()).add(a);
        }

        // Substance Domain: exactly 1 required
        List<ClassificationAssignment> subDomainList = byDimension.get(ClassificationDimension.SUBSTANCE_DOMAIN);
        if (subDomainList == null || subDomainList.size() != 1) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_CARDINALITY_VIOLATION,
                    "Exactly one SUBSTANCE_DOMAIN assignment required per profile, found: " + (subDomainList == null ? 0 : subDomainList.size()));
        }

        // Single-valued dimension cardinality checks
        for (Map.Entry<ClassificationDimension, List<ClassificationAssignment>> entry : byDimension.entrySet()) {
            if (entry.getKey().isSingleValued() && entry.getValue().size() > 1) {
                throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_CARDINALITY_VIOLATION,
                        "Dimension " + entry.getKey() + " allows at most 1 assignment, found: " + entry.getValue().size());
            }
        }

        // Prerequisites checks
        Set<String> codeValues = seenCodes.stream().map(ClassificationCode::getValue).collect(Collectors.toSet());
        if (codeValues.contains("BINARY_ACID") || codeValues.contains("OXYACID") || codeValues.contains("OTHER_ACID")) {
            if (!codeValues.contains("ACID") && !codeValues.contains("CARBOXYLIC_ACID")) {
                throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_PREREQUISITE_MISSING,
                        "Acid subtype requires ACID or CARBOXYLIC_ACID assignment");
            }
        }
        if (codeValues.contains("NORMAL_SALT") || codeValues.contains("ACID_SALT") || codeValues.contains("BASIC_SALT") ||
            codeValues.contains("DOUBLE_SALT") || codeValues.contains("HYDRATED_SALT") || codeValues.contains("OTHER_SALT")) {
            if (!codeValues.contains("SALT")) {
                throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_PREREQUISITE_MISSING,
                        "Salt subtype requires SALT assignment");
            }
        }

        // Sort assignments deterministically by dimension ordinal then code value
        List<ClassificationAssignment> sortedAssignments = new ArrayList<>(assignments);
        sortedAssignments.sort(Comparator.comparing((ClassificationAssignment a) -> a.getDimension().ordinal())
                .thenComparing(a -> a.getCode().getValue()));

        this.compoundId = compoundId;
        this.taxonomyVersion = taxonomyVersion;
        this.assignments = List.copyOf(sortedAssignments);
    }

    public CompoundId getCompoundId() { return compoundId; }
    public ClassificationTaxonomyVersion getTaxonomyVersion() { return taxonomyVersion; }
    public List<ClassificationAssignment> getAssignments() { return assignments; }

    public boolean hasClassification(ClassificationCode code) {
        return assignments.stream().anyMatch(a -> a.getCode().equals(code));
    }

    public Optional<ClassificationAssignment> getAssignment(ClassificationCode code) {
        return assignments.stream().filter(a -> a.getCode().equals(code)).findFirst();
    }
}

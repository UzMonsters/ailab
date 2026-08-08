package com.ailab.chemistry.domain.classification;

import java.util.*;

public final class ClassificationTaxonomy {
    private final ClassificationTaxonomyVersion version;
    private final Map<ClassificationCode, ClassificationDefinition> definitionsByCode;
    private final List<ClassificationDefinition> definitions;

    public ClassificationTaxonomy(ClassificationTaxonomyVersion version, List<ClassificationDefinition> definitions) {
        if (version == null) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_TAXONOMY_NOT_FOUND, "Taxonomy version cannot be null");
        }
        if (definitions == null || definitions.isEmpty()) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_TAXONOMY_NOT_FOUND, "Taxonomy definitions cannot be empty");
        }

        Map<ClassificationCode, ClassificationDefinition> byCode = new LinkedHashMap<>();
        for (ClassificationDefinition def : definitions) {
            if (byCode.containsKey(def.getCode())) {
                throw new ClassificationException(ClassificationErrorCode.DUPLICATE_CLASSIFICATION_ASSIGNMENT,
                        "Duplicate classification code in taxonomy: " + def.getCode());
            }
            byCode.put(def.getCode(), def);
        }

        // Parent integrity & cycle check
        for (ClassificationDefinition def : definitions) {
            if (def.getParentCode() != null) {
                if (!byCode.containsKey(def.getParentCode())) {
                    throw new ClassificationException(ClassificationErrorCode.INVALID_CLASSIFICATION_PARENT,
                            "Parent code " + def.getParentCode() + " not found for " + def.getCode());
                }
                // Cycle check
                Set<ClassificationCode> visited = new HashSet<>();
                visited.add(def.getCode());
                ClassificationCode currParent = def.getParentCode();
                while (currParent != null) {
                    if (visited.contains(currParent)) {
                        throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_HIERARCHY_CYCLE,
                                "Hierarchy cycle detected involving " + currParent);
                    }
                    visited.add(currParent);
                    ClassificationDefinition pDef = byCode.get(currParent);
                    currParent = pDef != null ? pDef.getParentCode() : null;
                }
            }
        }

        this.version = version;
        this.definitionsByCode = Collections.unmodifiableMap(byCode);
        this.definitions = List.copyOf(definitions);
    }

    public ClassificationTaxonomyVersion getVersion() { return version; }
    public List<ClassificationDefinition> getDefinitions() { return definitions; }

    public Optional<ClassificationDefinition> findDefinition(ClassificationCode code) {
        return Optional.ofNullable(definitionsByCode.get(code));
    }
}

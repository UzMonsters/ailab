package com.ailab.chemistry.domain.solubility;

import java.util.List;
import java.util.Objects;

public record SolubilityEquilibrium(
        SolubilityEquilibriumCode code,
        String solidCompoundCode,
        List<DissolutionTerm> terms,
        SolubilityProduct solubilityProduct,
        SolubilityReferenceConditions conditions,
        SolubilityDatasetVersion datasetVersion,
        SolubilityProvenance provenance
) {
    public SolubilityEquilibrium {
        Objects.requireNonNull(code, "code must not be null");
        if (solidCompoundCode == null || solidCompoundCode.isBlank()) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_EQUILIBRIUM_CODE, "Solid compound code is required");
        }
        solidCompoundCode = solidCompoundCode.trim().toUpperCase();
        terms = List.copyOf(Objects.requireNonNull(terms, "terms must not be null"));
        if (terms.size() < 2) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, "At least two aqueous ions are required");
        }
        Objects.requireNonNull(solubilityProduct, "solubilityProduct must not be null");
        Objects.requireNonNull(conditions, "conditions must not be null");
        Objects.requireNonNull(datasetVersion, "datasetVersion must not be null");
        Objects.requireNonNull(provenance, "provenance must not be null");
        int charge = terms.stream().mapToInt(t -> t.charge() * t.coefficient()).sum();
        if (charge != 0) {
            throw new SolubilityException(SolubilityErrorCode.UNBALANCED_DISSOLUTION, "Dissolution equation must be charge balanced");
        }
    }
}

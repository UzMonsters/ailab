package com.ailab.chemistry.api;

import java.util.List;
import java.util.UUID;

public interface CompoundCatalogService {
    CompoundDetails getById(UUID compoundId);
    CompoundDetails getByCode(String compoundCode);

    /**
     * Search by parser-normalized formula (preserves groups/hydrates, e.g. C2H5OH, CuSO4·5H2O).
     */
    List<CompoundSummary> findByNormalizedFormula(String formula);

    /**
     * Search by Hill-notation composition formula (derived from element counts, e.g. C2H6O for isomers, CuH10O9S for hydrate).
     */
    List<CompoundSummary> findByCompositionFormula(String formula);

    List<CompoundSummary> searchByName(String query);
    List<CompoundSummary> listCompounds();
}

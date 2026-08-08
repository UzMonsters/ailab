package com.ailab.chemistry.domain.compound;

import java.util.List;
import java.util.Optional;

public interface CompoundRepository {
    Optional<Compound> findById(CompoundId id);
    Optional<Compound> findByCode(CompoundCode code);
    List<Compound> findByNormalizedFormula(String normalizedFormula);
    List<Compound> findByCompositionFormula(String compositionFormula);
    List<Compound> searchByName(String query);
    List<Compound> findAll();
    long count();
    Compound save(Compound compound);
}

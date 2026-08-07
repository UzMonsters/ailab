package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Optional;

public interface AcidBaseReferenceService {

    ChemicalSpeciesDetails getSpecies(String speciesCode);

    List<DissociationStepDetails> getDissociationSteps(String speciesCode);

    Optional<EquilibriumConstantDetails> findKa(
            String speciesCode,
            Temperature temperature,
            String solventCode
    );

    Optional<EquilibriumConstantDetails> findKb(
            String speciesCode,
            Temperature temperature,
            String solventCode
    );

    ConjugatePairDetails getConjugatePair(String speciesCode);
}

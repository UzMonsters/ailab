package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Optional;

public interface AcidBaseReferenceRepository {

    Optional<ChemicalSpecies> findSpeciesByCode(ChemicalSpeciesCode code);

    List<ChemicalSpecies> findAllSpecies();

    List<DissociationStep> findDissociationStepsForSpecies(ChemicalSpeciesCode code);

    Optional<EquilibriumConstant> findKa(ChemicalSpeciesCode code, Temperature temperature, String solventCode);

    Optional<EquilibriumConstant> findKb(ChemicalSpeciesCode code, Temperature temperature, String solventCode);

    Optional<ConjugatePair> findConjugatePair(ChemicalSpeciesCode code);
}

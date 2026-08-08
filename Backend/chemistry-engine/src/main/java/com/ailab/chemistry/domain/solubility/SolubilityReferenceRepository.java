package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Optional;

public interface SolubilityReferenceRepository {
    Optional<SolubilityEquilibrium> findByCode(SolubilityEquilibriumCode code, Temperature temperature, String solventCode);
    List<SolubilityEquilibrium> findAll();
}

package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;

import java.math.BigDecimal;
import java.util.List;

public record MolarSolubilityRequest(
        SolubilityEquilibrium equilibrium,
        List<IonicSpeciesConcentration> initialIons,
        List<IonicSpeciesConcentration> spectatorIons,
        ActivityParameterSet activityParameterSet,
        BigDecimal comparisonTolerance
) {
    public MolarSolubilityRequest {
        initialIons = List.copyOf(initialIons == null ? List.of() : initialIons);
        spectatorIons = List.copyOf(spectatorIons == null ? List.of() : spectatorIons);
        comparisonTolerance = comparisonTolerance == null ? new BigDecimal("1e-8") : comparisonTolerance;
    }
}

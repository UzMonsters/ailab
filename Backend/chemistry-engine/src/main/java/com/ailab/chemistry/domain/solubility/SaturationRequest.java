package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.List;

public record SaturationRequest(
        SolubilityEquilibrium equilibrium,
        String equilibriumCode,
        List<IonicSpeciesConcentration> dissolvedIons,
        List<IonicSpeciesConcentration> spectatorIons,
        ActivityParameterSet activityParameterSet,
        Temperature temperature,
        String solventCode,
        ActivityModel activityModel,
        BigDecimal comparisonTolerance
) {
    public SaturationRequest(SolubilityEquilibrium equilibrium, List<IonicSpeciesConcentration> dissolvedIons,
                             List<IonicSpeciesConcentration> spectatorIons, ActivityParameterSet activityParameterSet,
                             BigDecimal comparisonTolerance) {
        this(equilibrium, null, dissolvedIons, spectatorIons, activityParameterSet,
                equilibrium.conditions().temperature(), equilibrium.conditions().solventCode(), activityParameterSet.model(), comparisonTolerance);
    }

    public SaturationRequest {
        dissolvedIons = List.copyOf(dissolvedIons == null ? List.of() : dissolvedIons);
        spectatorIons = List.copyOf(spectatorIons == null ? List.of() : spectatorIons);
        comparisonTolerance = comparisonTolerance == null ? new BigDecimal("1e-8") : comparisonTolerance;
    }

    public static SaturationRequest forEquilibriumCode(String equilibriumCode, List<IonicSpeciesConcentration> dissolvedIons,
                                                       List<IonicSpeciesConcentration> spectatorIons, Temperature temperature,
                                                       String solventCode, ActivityModel activityModel) {
        return new SaturationRequest(null, equilibriumCode, dissolvedIons, spectatorIons, null, temperature, solventCode, activityModel, new BigDecimal("1e-8"));
    }
}

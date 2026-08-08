package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicStrength;
import com.ailab.chemistry.domain.measurement.MolarConcentration;

import java.math.BigDecimal;
import java.util.Map;

public record MolarSolubilityResult(
        SolubilityEquilibrium equilibrium,
        ActivityModel activityModel,
        MolarConcentration molarSolubility,
        Map<String, BigDecimal> equilibriumConcentrations,
        SaturationStatus saturationStatus,
        IonicStrength ionicStrength,
        int iterations,
        SolubilityResidual residual,
        SolubilitySolverStatus solverStatus
) {}

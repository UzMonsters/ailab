package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicStrength;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public record PrecipitationResult(
        SolubilityEquilibrium equilibrium,
        ActivityModel activityModel,
        SaturationStatus initialStatus,
        IonProduct initialIonProduct,
        AmountOfSubstance precipitatedMoles,
        Optional<Mass> precipitatedMass,
        Map<String, BigDecimal> equilibriumConcentrations,
        IonProduct finalIonProduct,
        IonicStrength ionicStrength,
        int iterations,
        SolubilityResidual residual,
        SolubilitySolverStatus solverStatus
) {}

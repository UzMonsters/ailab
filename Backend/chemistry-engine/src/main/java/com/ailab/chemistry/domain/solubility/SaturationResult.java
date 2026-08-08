package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicStrength;

import java.math.BigDecimal;
import java.util.Map;

public record SaturationResult(
        SolubilityEquilibrium equilibrium,
        ActivityModel activityModel,
        IonProduct ionicProduct,
        SaturationRatio saturationRatio,
        SaturationStatus status,
        IonicStrength ionicStrength,
        Map<String, BigDecimal> activities,
        int iterations,
        SolubilityResidual residual
) {}

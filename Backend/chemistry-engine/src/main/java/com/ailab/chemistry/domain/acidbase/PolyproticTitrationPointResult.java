package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Volume;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record PolyproticTitrationPointResult(
        Volume addedTitrantVolume,
        Volume totalVolume,
        PhValue ph,
        PhValue poh,
        BigDecimal hydroniumConcentration,
        BigDecimal hydroxideConcentration,
        MolarConcentration analyticalFamilyConcentration,
        BigDecimal fixedSpectatorChargeConcentration,
        SpeciesDistribution distribution,
        PolyproticTitrationRegion region,
        PolyproticTitrationMethod method,
        Map<String, BigDecimal> constants,
        List<PolyproticAssumption> assumptions,
        PolyproticTitrationResidual residual,
        PolyproticSolverStatus solverStatus
) {
    public PolyproticTitrationPointResult {
        Objects.requireNonNull(addedTitrantVolume, "addedTitrantVolume must not be null");
        Objects.requireNonNull(totalVolume, "totalVolume must not be null");
        Objects.requireNonNull(ph, "ph must not be null");
        Objects.requireNonNull(poh, "poh must not be null");
        Objects.requireNonNull(hydroniumConcentration, "hydroniumConcentration must not be null");
        Objects.requireNonNull(hydroxideConcentration, "hydroxideConcentration must not be null");
        Objects.requireNonNull(analyticalFamilyConcentration, "analyticalFamilyConcentration must not be null");
        Objects.requireNonNull(fixedSpectatorChargeConcentration, "fixedSpectatorChargeConcentration must not be null");
        Objects.requireNonNull(distribution, "distribution must not be null");
        Objects.requireNonNull(region, "region must not be null");
        Objects.requireNonNull(method, "method must not be null");
        constants = Map.copyOf(Objects.requireNonNull(constants, "constants must not be null"));
        assumptions = List.copyOf(Objects.requireNonNull(assumptions, "assumptions must not be null"));
        Objects.requireNonNull(residual, "residual must not be null");
        Objects.requireNonNull(solverStatus, "solverStatus must not be null");
    }
}

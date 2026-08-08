package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public record ElectrochemicalActivity(
        String speciesCode,
        String phase,
        ElectrochemicalActivityBasis basis,
        BigDecimal value,
        Integer charge,
        BigDecimal ionicStrength
) {
    public static ElectrochemicalActivity explicit(String speciesCode, String phase, BigDecimal activity) {
        return new ElectrochemicalActivity(speciesCode, phase, ElectrochemicalActivityBasis.EXPLICIT_DIMENSIONLESS_ACTIVITY, activity, null, null);
    }

    public static ElectrochemicalActivity pureSolid(String speciesCode, String phase) {
        return new ElectrochemicalActivity(speciesCode, phase, ElectrochemicalActivityBasis.PURE_SOLID, BigDecimal.ONE, null, null);
    }

    public static ElectrochemicalActivity pureLiquid(String speciesCode, String phase) {
        return new ElectrochemicalActivity(speciesCode, phase, ElectrochemicalActivityBasis.PURE_LIQUID, BigDecimal.ONE, null, null);
    }

    public static ElectrochemicalActivity idealGas(String speciesCode, BigDecimal partialPressureBar) {
        return new ElectrochemicalActivity(speciesCode, "GAS", ElectrochemicalActivityBasis.IDEAL_GAS_PARTIAL_PRESSURE, partialPressureBar, null, null);
    }

    public static ElectrochemicalActivity aqueousIdeal(String speciesCode, BigDecimal concentrationMolar) {
        return new ElectrochemicalActivity(speciesCode, "AQUEOUS", ElectrochemicalActivityBasis.AQUEOUS_IDEAL, concentrationMolar, null, null);
    }

    public static ElectrochemicalActivity aqueousDavies(String speciesCode, BigDecimal concentrationMolar, int charge, BigDecimal ionicStrength) {
        return new ElectrochemicalActivity(speciesCode, "AQUEOUS", ElectrochemicalActivityBasis.AQUEOUS_DAVIES, concentrationMolar, charge, ionicStrength);
    }
}

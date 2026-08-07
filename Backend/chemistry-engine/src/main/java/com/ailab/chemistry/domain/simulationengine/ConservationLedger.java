package com.ailab.chemistry.domain.simulationengine;

import java.util.EnumMap;
import java.util.Map;

public record ConservationLedger(Map<ConservationDimension, ConservationResidual> residuals) {
    public ConservationLedger {
        EnumMap<ConservationDimension, ConservationResidual> copy = new EnumMap<>(ConservationDimension.class);
        if (residuals != null) {
            copy.putAll(residuals);
        }
        residuals = Map.copyOf(copy);
    }

    public static ConservationLedger notApplicable() {
        EnumMap<ConservationDimension, ConservationResidual> residuals = new EnumMap<>(ConservationDimension.class);
        residuals.put(ConservationDimension.MATERIAL_AMOUNT,
                new ConservationResidual(ConservationStatus.NOT_APPLICABLE, null, null, "mol"));
        return new ConservationLedger(residuals);
    }

    public static ConservationLedger satisfiedFor(SimulationOperationType operationType) {
        EnumMap<ConservationDimension, ConservationResidual> residuals = new EnumMap<>(ConservationDimension.class);
        residuals.put(ConservationDimension.MATERIAL_AMOUNT,
                new ConservationResidual(ConservationStatus.SATISFIED, null, null, "mol"));
        residuals.put(ConservationDimension.VESSEL_VOLUME,
                new ConservationResidual(ConservationStatus.SATISFIED, null, null, "mL"));
        switch (operationType) {
            case STOICHIOMETRIC_REACTION, EQUILIBRIUM_REACTION, KINETIC_PROGRESS, PHASE_TRANSITION ->
                    residuals.put(ConservationDimension.ELEMENT_ATOMS,
                            new ConservationResidual(ConservationStatus.SATISFIED, null, null, "mol-atoms"));
            case ELECTROLYSIS -> {
                residuals.put(ConservationDimension.ELECTRIC_CHARGE,
                        new ConservationResidual(ConservationStatus.SATISFIED, null, null, "C"));
                residuals.put(ConservationDimension.ELECTRON_AMOUNT,
                        new ConservationResidual(ConservationStatus.NOT_APPLICABLE, null, null, "mol"));
            }
            case THERMAL_OPERATION ->
                    residuals.put(ConservationDimension.ENERGY,
                            new ConservationResidual(ConservationStatus.SATISFIED, null, null, "J"));
            case GAS_STATE_CHANGE ->
                    residuals.put(ConservationDimension.ELEMENT_ATOMS,
                            new ConservationResidual(ConservationStatus.NOT_EVALUATED, null, null, "mol-atoms"));
            case BOOKKEEPING_MIX -> {
            }
        }
        return new ConservationLedger(residuals);
    }

    public ConservationResidual residual(ConservationDimension dimension) {
        return residuals.getOrDefault(dimension,
                new ConservationResidual(ConservationStatus.NOT_EVALUATED, null, null, ""));
    }
}

package com.ailab.chemistry.infrastructure.persistence.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.phasebehavior.AntoineCoefficientSet;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorRepository;
import com.ailab.chemistry.domain.phasebehavior.PhaseBoundaryPoint;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionConditions;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionEvidenceStatus;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionProvenance;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRecord;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionType;
import com.ailab.chemistry.domain.phasebehavior.TransitionEnthalpy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public final class InMemoryPhaseBehaviorRepository implements PhaseBehaviorRepository {
    private final List<PhaseTransitionRecord> transitions;
    private final List<AntoineCoefficientSet> antoine;
    private final List<PhaseBoundaryPoint> triplePoints;
    private final List<PhaseBoundaryPoint> criticalPoints;

    private InMemoryPhaseBehaviorRepository(
            List<PhaseTransitionRecord> transitions,
            List<AntoineCoefficientSet> antoine,
            List<PhaseBoundaryPoint> triplePoints,
            List<PhaseBoundaryPoint> criticalPoints) {
        this.transitions = List.copyOf(transitions);
        this.antoine = List.copyOf(antoine);
        this.triplePoints = List.copyOf(triplePoints);
        this.criticalPoints = List.copyOf(criticalPoints);
    }

    public static InMemoryPhaseBehaviorRepository reference() {
        PhaseTransitionProvenance nist = new PhaseTransitionProvenance("NIST-WEBBOOK", "NIST Chemistry WebBook phase-change and Antoine records", "NIST SRD public data terms", PhaseTransitionEvidenceStatus.SOURCED_REFERENCE_VALUE);
        PhaseTransitionProvenance yaws = new PhaseTransitionProvenance("YAWS-HANDBOOK", "Yaws' Handbook of Antoine Coefficients", "citation required", PhaseTransitionEvidenceStatus.SOURCED_CORRELATION);
        return new InMemoryPhaseBehaviorRepository(
                List.of(
                        transition("PT-H2O-FUSION-1ATM", "COMP-H2O", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID, "273.15", "1", "6.011", "kJ/mol", nist),
                        transition("PT-H2O-VAPORIZATION-1ATM", "COMP-H2O", PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS, "373.15", "1", "40.65", "kJ/mol", nist),
                        transition("PT-ETHANOL-VAPORIZATION-1ATM", "COMP-ETHANOL", PhaseTransitionType.VAPORIZATION, MatterState.LIQUID, MatterState.GAS, "351.44", "1", "38.56", "kJ/mol", nist),
                        transition("PT-CO2-SUBLIMATION-1ATM", "COMP-CO2", PhaseTransitionType.SUBLIMATION, MatterState.SOLID, MatterState.GAS, "194.67", "1", "25.2", "kJ/mol", nist)
                ),
                List.of(
                        new AntoineCoefficientSet("VP-H2O-ANTOINE-1-100C", "COMP-H2O", MatterState.LIQUID, MatterState.GAS,
                                new BigDecimal("8.07131"), new BigDecimal("1730.63"), new BigDecimal("233.426"),
                                Temperature.of("274.15", TemperatureUnit.KELVIN), Temperature.of("373.15", TemperatureUnit.KELVIN),
                                "degC", "mmHg", "log10(P_mmHg)=A-B/(C+T_degC)", yaws),
                        new AntoineCoefficientSet("VP-ETHANOL-ANTOINE--57-80C", "COMP-ETHANOL", MatterState.LIQUID, MatterState.GAS,
                                new BigDecimal("8.20417"), new BigDecimal("1642.89"), new BigDecimal("230.300"),
                                Temperature.of("216.15", TemperatureUnit.KELVIN), Temperature.of("353.15", TemperatureUnit.KELVIN),
                                "degC", "mmHg", "log10(P_mmHg)=A-B/(C+T_degC)", yaws)
                ),
                List.of(
                        boundary("COMP-H2O", "273.16", Pressure.of("611.657", PressureUnit.PASCAL), "TRIPLE_POINT", nist),
                        boundary("COMP-CO2", "216.592", Pressure.of("5.185", PressureUnit.BAR), "TRIPLE_POINT", nist)
                ),
                List.of(
                        boundary("COMP-H2O", "647.096", Pressure.of("220.64", PressureUnit.BAR), "CRITICAL_POINT", nist),
                        boundary("COMP-ETHANOL", "514.0", Pressure.of("61.4", PressureUnit.BAR), "CRITICAL_POINT", nist),
                        boundary("COMP-CO2", "304.1282", Pressure.of("73.773", PressureUnit.BAR), "CRITICAL_POINT", nist)
                ));
    }

    @Override
    public Optional<PhaseTransitionRecord> findTransition(String compoundCode, PhaseTransitionType forwardType) {
        return transitions.stream()
                .filter(r -> r.compoundCode().equals(compoundCode) && r.transitionType() == forwardType.forwardType())
                .findFirst();
    }

    @Override
    public Optional<AntoineCoefficientSet> findAntoine(String compoundCode, MatterState initialPhase, MatterState finalPhase) {
        return antoine.stream()
                .filter(c -> c.compoundCode().equals(compoundCode) && c.initialPhase() == initialPhase && c.finalPhase() == finalPhase)
                .findFirst();
    }

    @Override
    public Optional<PhaseBoundaryPoint> findTriplePoint(String compoundCode) {
        return triplePoints.stream().filter(p -> p.compoundCode().equals(compoundCode)).findFirst();
    }

    @Override
    public Optional<PhaseBoundaryPoint> findCriticalPoint(String compoundCode) {
        return criticalPoints.stream().filter(p -> p.compoundCode().equals(compoundCode)).findFirst();
    }

    private static PhaseTransitionRecord transition(
            String id,
            String compoundCode,
            PhaseTransitionType type,
            MatterState initial,
            MatterState fin,
            String tempK,
            String pressureAtm,
            String enthalpyKjPerMol,
            String originalUnit,
            PhaseTransitionProvenance provenance) {
        return new PhaseTransitionRecord(
                id,
                compoundCode,
                type,
                initial,
                fin,
                new PhaseTransitionConditions(Temperature.of(tempK, TemperatureUnit.KELVIN), Pressure.of(pressureAtm, PressureUnit.ATMOSPHERE)),
                new TransitionEnthalpy(MolarEnergy.of(enthalpyKjPerMol, MolarEnergyUnit.KILOJOULE_PER_MOLE), enthalpyKjPerMol, originalUnit, null),
                provenance);
    }

    private static PhaseBoundaryPoint boundary(String compoundCode, String tempK, Pressure pressure, String type, PhaseTransitionProvenance provenance) {
        return new PhaseBoundaryPoint(compoundCode, Temperature.of(tempK, TemperatureUnit.KELVIN), pressure, type, provenance);
    }
}

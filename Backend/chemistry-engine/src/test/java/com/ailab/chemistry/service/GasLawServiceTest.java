package com.ailab.chemistry.service;

import com.ailab.chemistry.api.GasLawService;
import com.ailab.chemistry.api.PhaseBehaviorService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.gas.GasEquationModel;
import com.ailab.chemistry.domain.gas.GasStateRequest;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorErrorCode;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorException;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorRepository;
import com.ailab.chemistry.domain.phasebehavior.AntoineCoefficientSet;
import com.ailab.chemistry.domain.phasebehavior.PhaseBoundaryPoint;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRecord;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRequest;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionType;
import com.ailab.chemistry.infrastructure.persistence.phasebehavior.InMemoryPhaseBehaviorRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GasLawServiceTest {

    @Test
    void serviceImplementationsDelegateToPureCalculators() {
        GasLawService gasLawService = new GasLawServiceImpl();

        assertThat(gasLawService.calculateState(GasStateRequest.solveVolume(
                GasEquationModel.IDEAL_GAS,
                Pressure.of("101325", PressureUnit.PASCAL),
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                null)).state().volume()).isNotNull();
    }

    @Test
    void phaseBehaviorServiceUsesExplicitRepositoryAndFailsFastWhenUnavailable() {
        PhaseBehaviorService service = new PhaseBehaviorServiceImpl(InMemoryPhaseBehaviorRepository.reference());
        assertThat(service.calculateTransition(new PhaseTransitionRequest(
                        "COMP-H2O", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID,
                        AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                        Temperature.of("273.15", TemperatureUnit.KELVIN),
                        Pressure.of("1", PressureUnit.ATMOSPHERE)))
                .heat().in(EnergyUnit.JOULE)).isPositive();

        PhaseBehaviorService unavailable = new PhaseBehaviorServiceImpl(new PhaseBehaviorRepository() {
            @Override
            public Optional<PhaseTransitionRecord> findTransition(String compoundCode, PhaseTransitionType forwardType) {
                return Optional.empty();
            }

            @Override
            public Optional<AntoineCoefficientSet> findAntoine(String compoundCode, MatterState initialPhase, MatterState finalPhase) {
                return Optional.empty();
            }

            @Override
            public Optional<PhaseBoundaryPoint> findTriplePoint(String compoundCode) {
                return Optional.empty();
            }

            @Override
            public Optional<PhaseBoundaryPoint> findCriticalPoint(String compoundCode) {
                return Optional.empty();
            }
        });
        assertThatThrownBy(() -> unavailable.calculateTransition(new PhaseTransitionRequest(
                "COMP-H2O", PhaseTransitionType.FUSION, MatterState.SOLID, MatterState.LIQUID,
                AmountOfSubstance.of("1", AmountOfSubstanceUnit.MOLE),
                Temperature.of("273.15", TemperatureUnit.KELVIN),
                Pressure.of("1", PressureUnit.ATMOSPHERE))))
                .isInstanceOf(PhaseBehaviorException.class)
                .extracting("errorCode")
                .isEqualTo(PhaseBehaviorErrorCode.MISSING_TRANSITION_RECORD);
    }
}

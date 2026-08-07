package com.ailab.chemistry.service;

import com.ailab.chemistry.api.EquilibriumCompositionService;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ThermodynamicEquilibriumService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionErrorCode;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionException;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionMethod;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionRequest;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionResult;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionStatus;
import com.ailab.chemistry.domain.thermodynamics.InitialParticipantAmount;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryTemperatureCorrelationRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquilibriumCompositionServiceTest {

    private EquilibriumCompositionService service;

    @BeforeEach
    void setUp() {
        var reactionCatalog = new ReactionCatalogServiceImpl(new InMemoryReactionRepository());
        var thermoRefRepo = new InMemoryThermodynamicReferenceRepository();
        var thermoCorrRepo = new InMemoryTemperatureCorrelationRepository();
        var referenceService = new ThermodynamicReferenceServiceImpl(thermoRefRepo);
        var reactionThermodynamics = new ReactionThermodynamicsServiceImpl(reactionCatalog, referenceService);
        var tempDependent = new TemperatureDependentThermodynamicsServiceImpl(reactionCatalog, referenceService, reactionThermodynamics, thermoCorrRepo);

        ThermodynamicEquilibriumService equilibriumService = new ThermodynamicEquilibriumServiceImpl(
                reactionCatalog, reactionThermodynamics, tempDependent, null);

        service = new EquilibriumCompositionServiceImpl(reactionCatalog, equilibriumService, null);
    }

    @Test
    void calculateWaterVapourFormationEquilibrium() {
        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-WATER-SYNTHESIS",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-H2", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-H2O", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult result = service.calculate(request);

        assertThat(result.status()).isIn(EquilibriumCompositionStatus.CONVERGED, EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT);
        assertThat(result.extent().extent()).isGreaterThan(new BigDecimal("0.99"));
        assertThat(result.residual().maxMassBalanceError()).isLessThan(new BigDecimal("1e-10"));
    }

    @Test
    void calculateCoOxidationEquilibrium() {
        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult result = service.calculate(request);

        assertThat(result.status()).isIn(EquilibriumCompositionStatus.CONVERGED, EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT);
        assertThat(result.extent().extent()).isGreaterThan(new BigDecimal("0.99"));
    }

    @Test
    void calculateMethaneCombustionEquilibrium() {
        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-METHANE-COMBUSTION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CH4", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.0")),
                        new InitialParticipantAmount("COMP-H2O", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult result = service.calculate(request);

        assertThat(result.status()).isIn(EquilibriumCompositionStatus.CONVERGED, EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT);
        assertThat(result.extent().extent()).isGreaterThan(new BigDecimal("0.99"));
    }

    @Test
    void unknownReactionThrowsException() {
        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-UNKNOWN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-H2", MatterState.GAS, new BigDecimal("1.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        assertThatThrownBy(() -> service.calculate(request))
                .isInstanceOf(EquilibriumCompositionException.class)
                .extracting("errorCode")
                .isEqualTo(EquilibriumCompositionErrorCode.UNKNOWN_REACTION);
    }
}

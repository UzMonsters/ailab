package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ThermodynamicEquilibriumService;
import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectedEquilibriumResult;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionRequest;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionResult;
import com.ailab.chemistry.domain.acidbase.ActivityErrorCode;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.acidbase.IonicStrength;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ActivityBasis;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCalculationStatus;
import com.ailab.chemistry.domain.thermodynamics.NonstandardGibbsRequest;
import com.ailab.chemistry.domain.thermodynamics.ParticipantActivity;
import com.ailab.chemistry.domain.thermodynamics.PhaseStabilityStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionActivityInput;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicDirection;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryTemperatureCorrelationRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ThermodynamicEquilibriumServiceTest {
    private final ThermodynamicEquilibriumService service = service();

    @Test
    void coOxidationHasLargePositiveStandardLnKAt298K() {
        var result = service.calculateStandardConstant("RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.CALCULABLE);
        assertThat(result.standardConstant().lnK()).isCloseTo(new BigDecimal("207.499361"), offset("0.000001"));
        assertThat(result.method().name()).contains("PHASE8B");
    }

    @Test
    void methaneCombustionAtSupportedTemperatureIsDeterministic() {
        var first = service.calculateStandardConstant("RXN-METHANE-COMBUSTION",
                Temperature.of("600.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());
        var second = service.calculateStandardConstant("RXN-METHANE-COMBUSTION",
                Temperature.of("600.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());

        assertThat(first.status()).isEqualTo(EquilibriumCalculationStatus.CALCULABLE);
        assertThat(second.standardConstant().lnK()).isEqualByComparingTo(first.standardConstant().lnK());
    }

    @Test
    void waterVapourFormationCalculatesNonstandardGibbsFromExplicitGasActivities() {
        var result = service.calculateNonstandardGibbs(new NonstandardGibbsRequest(
                "RXN-WATER-SYNTHESIS",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of(),
                ReactionActivityInput.of(List.of(
                        ParticipantActivity.idealGasPartialPressure("COMP-H2O", Pressure.of("0.200", PressureUnit.BAR), Pressure.of("1.000", PressureUnit.BAR)),
                        ParticipantActivity.idealGasPartialPressure("COMP-H2", Pressure.of("0.100", PressureUnit.BAR), Pressure.of("1.000", PressureUnit.BAR)),
                        ParticipantActivity.idealGasPartialPressure("COMP-O2", Pressure.of("0.210", PressureUnit.BAR), Pressure.of("1.000", PressureUnit.BAR))
                ))));

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.CALCULABLE);
        assertThat(result.reactionQuotient().lnQ()).isCloseTo(new BigDecimal("2.946942"), offset("0.000001"));
        assertThat(result.deltaGibbsKjPerMol()).isCloseTo(new BigDecimal("-449.838657"), offset("0.000001"));
        assertThat(result.direction()).isEqualTo(ThermodynamicDirection.FORWARD_THERMODYNAMIC_DRIVING_FORCE);
    }

    @Test
    void liquidWaterAt400KIsReportedAsPrescribedPhaseAssumption() {
        var result = service.calculateStandardConstant("RXN-WATER-SYNTHESIS",
                Temperature.of("400.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of("COMP-H2O", MatterState.LIQUID));

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.CALCULABLE);
        assertThat(result.phaseStabilityStatus()).isEqualTo(PhaseStabilityStatus.PRESCRIBED_PHASE_ASSUMPTION);
    }

    @Test
    void unsupportedTemperatureReportsIncompleteCoverageWithoutExtrapolation() {
        var result = service.calculateStandardConstant("RXN-WATER-SYNTHESIS",
                Temperature.of("400.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().outOfRangeCorrelations()).contains("COMP-H2O|GAS");
    }

    @Test
    void aqueousDaviesInputIsStructuredButCannotFabricateMissingAqueousThermodynamics() {
        var result = service.calculateNonstandardGibbs(new NonstandardGibbsRequest(
                "RXN-NEUT-HCL-NAOH",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of(),
                ReactionActivityInput.of(List.of(
                        ParticipantActivity.aqueous("COMP-HCL", "SPEC-HCL", new BigDecimal("0.10"), BigDecimal.ONE, ActivityBasis.AQUEOUS_DAVIES),
                        ParticipantActivity.aqueous("COMP-NAOH", "SPEC-NAOH", new BigDecimal("0.10"), BigDecimal.ONE, ActivityBasis.AQUEOUS_DAVIES)
                ))));

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().unsupportedStates()).isNotEmpty();
    }

    @Test
    void aqueousDaviesValidityFailureIsReportedFromIonicActivityService() {
        var service = service(new FailingDaviesActivityService());

        var result = service.calculateNonstandardGibbs(new NonstandardGibbsRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of(),
                ReactionActivityInput.of(List.of(
                        ParticipantActivity.aqueous("COMP-CO", "SPEC-CO", new BigDecimal("1.0"), BigDecimal.ONE,
                                ActivityBasis.AQUEOUS_DAVIES, 1)
                ))));

        assertThat(result.status()).isEqualTo(EquilibriumCalculationStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().unsupportedStates()).contains("AQUEOUS_DAVIES_VALIDITY_FAILURE|OUTSIDE_MODEL_VALIDITY_RANGE");
    }

    private static ThermodynamicEquilibriumService service() {
        return service(null);
    }

    private static ThermodynamicEquilibriumService service(IonicActivityService ionicActivityService) {
        var reactionCatalog = new ReactionCatalogServiceImpl(new InMemoryReactionRepository());
        var reference = new ThermodynamicReferenceServiceImpl(new InMemoryThermodynamicReferenceRepository());
        var reactionThermodynamics = new ReactionThermodynamicsServiceImpl(reactionCatalog, reference);
        var temperatureDependent = new TemperatureDependentThermodynamicsServiceImpl(reactionCatalog, reference,
                reactionThermodynamics, new InMemoryTemperatureCorrelationRepository());
        return new ThermodynamicEquilibriumServiceImpl(reactionCatalog, reactionThermodynamics, temperatureDependent, ionicActivityService);
    }

    private static org.assertj.core.data.Offset<BigDecimal> offset(String value) {
        return org.assertj.core.data.Offset.offset(new BigDecimal(value));
    }

    private static final class FailingDaviesActivityService implements IonicActivityService {
        @Override
        public IonicStrength calculateIonicStrength(List<IonicSpeciesConcentration> species) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ActivityCorrectionResult calculateActivities(List<IonicSpeciesConcentration> species, Temperature temperature,
                                                            String solventCode, ActivityModel model) {
            throw new ActivityException(ActivityErrorCode.OUTSIDE_MODEL_VALIDITY_RANGE, "outside Davies range");
        }

        @Override
        public ActivityCorrectedEquilibriumResult calculateEquilibrium(ActivityCorrectionRequest request) {
            throw new UnsupportedOperationException();
        }
    }
}

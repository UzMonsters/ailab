package com.ailab.chemistry.service;

import com.ailab.chemistry.api.BufferCalculationService;
import com.ailab.chemistry.domain.acidbase.BufferCalculationRequest;
import com.ailab.chemistry.domain.acidbase.BufferCalculationResult;
import com.ailab.chemistry.domain.acidbase.BufferErrorCode;
import com.ailab.chemistry.domain.acidbase.BufferException;
import com.ailab.chemistry.domain.acidbase.BufferPreparationRequest;
import com.ailab.chemistry.domain.acidbase.BufferPreparationResult;
import com.ailab.chemistry.domain.acidbase.BufferRegionStatus;
import com.ailab.chemistry.domain.acidbase.BufferSystem;
import com.ailab.chemistry.domain.acidbase.BufferSystemType;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BufferCalculationServiceTest {

    private BufferCalculationService service;

    @BeforeEach
    void setUp() {
        var referenceRepository = new InMemoryAcidBaseReferenceRepository();
        var referenceService = new AcidBaseReferenceServiceImpl(referenceRepository);
        var equilibriumService = new AcidBaseEquilibriumServiceImpl(referenceService);
        service = new BufferCalculationServiceImpl(referenceService, equilibriumService);
    }

    @Test
    void calculatesAceticAcidAcetateBufferFromCatalogueConstants() {
        BufferCalculationResult result = service.calculateBuffer(BufferCalculationRequest.fromSpeciesAmounts(
                "SPEC-CH3COOH",
                "SPEC-CH3COO-MINUS",
                amount("0.100"),
                amount("0.100"),
                Volume.of("1.0", VolumeUnit.LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        ));

        assertThat(result.getSystem().getSystemType()).isEqualTo(BufferSystemType.WEAK_ACID_CONJUGATE_BASE);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(result.getConstants()).containsEntry("Ka", new BigDecimal("0.0000175"));
        assertThat(result.getSources()).isNotEmpty();
    }

    @Test
    void calculatesAmmoniaAmmoniumBufferUsingPkwAndKb() {
        BufferCalculationResult result = service.calculateBuffer(BufferCalculationRequest.fromSpeciesAmounts(
                "SPEC-NH4-PLUS",
                "SPEC-NH3",
                amount("0.100"),
                amount("0.100"),
                Volume.of("1.0", VolumeUnit.LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        ));

        assertThat(result.getSystem().getSystemType()).isEqualTo(BufferSystemType.WEAK_BASE_CONJUGATE_ACID);
        assertThat(result.getPoh().getValue()).isCloseTo(new BigDecimal("4.7545"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("9.2455"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
    }

    @Test
    void preparationAndPerturbationUseValidatedConjugatePairs() {
        BufferSystem system = service.resolveBufferSystem(
                "SPEC-CH3COOH",
                "SPEC-CH3COO-MINUS",
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        );

        BufferPreparationResult preparation = service.calculatePreparation(new BufferPreparationRequest(
                system,
                new BigDecimal("5.7567"),
                new BigDecimal("0.110"),
                Volume.of("1.0", VolumeUnit.LITER)
        ));
        assertThat(preparation.getStatus()).isEqualTo(BufferRegionStatus.OUTSIDE_RECOMMENDED_BUFFER_RANGE);
        assertThat(preparation.getBaseComponentConcentration().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("0.100"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
    }

    @Test
    void rejectsInvalidPairsReversedPairsMissingTemperatureAndUnsupportedSolvent() {
        Temperature t25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);

        assertThatThrownBy(() -> service.resolveBufferSystem("SPEC-CH3COOH", "SPEC-NH3", t25, "COMP-H2O"))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.INVALID_CONJUGATE_PAIR);

        assertThatThrownBy(() -> service.resolveBufferSystem("SPEC-CH3COO-MINUS", "SPEC-CH3COOH", t25, "COMP-H2O"))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.INVALID_CONJUGATE_PAIR_ORIENTATION);

        assertThatThrownBy(() -> service.resolveBufferSystem("SPEC-CH3COOH", "SPEC-CH3COO-MINUS", Temperature.of("20.0", TemperatureUnit.CELSIUS), "COMP-H2O"))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA);

        assertThatThrownBy(() -> service.resolveBufferSystem("SPEC-CH3COOH", "SPEC-CH3COO-MINUS", t25, "COMP-ETHANOL"))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.UNSUPPORTED_SOLVENT);

        assertThatThrownBy(() -> service.resolveBufferSystem("SPEC-HCO3-MINUS", "SPEC-CO3-2MINUS", t25, "COMP-H2O"))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.POLYPROTIC_BUFFER_UNSUPPORTED);
    }

    @Test
    void exactExhaustionDelegatesAndExcessStrongReagentDoesNotFabricatePh() {
        BufferCalculationRequest initial = BufferCalculationRequest.fromSpeciesAmounts(
                "SPEC-CH3COOH",
                "SPEC-CH3COO-MINUS",
                amount("0.100"),
                amount("0.100"),
                Volume.of("1.0", VolumeUnit.LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        );

        var exact = service.addStrongAcidOrBase(com.ailab.chemistry.domain.acidbase.BufferPerturbationRequest.strongAcidWithFinalVolume(
                initial,
                amount("0.100"),
                Volume.of("2.0", VolumeUnit.LITER)
        ));
        assertThat(exact.getStatus()).isEqualTo(BufferRegionStatus.EXACT_EXHAUSTION);
        assertThat(exact.getDelegatedEquilibriumResult()).isPresent();
        assertThat(exact.getDelegatedEquilibriumResult().orElseThrow().getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));

        var excess = service.addStrongAcidOrBase(com.ailab.chemistry.domain.acidbase.BufferPerturbationRequest.strongAcidNegligibleVolume(initial, amount("0.101")));
        assertThat(excess.getStatus()).isEqualTo(BufferRegionStatus.EXCESS_STRONG_ACID_UNSUPPORTED);
        assertThat(excess.getDelegatedEquilibriumResult()).isEmpty();
        assertThat(excess.getExplanation()).contains("mixed-equilibrium solver");
    }

    @Test
    void unsupportedFutureFeaturesAreAbsentFromPublicService() {
        assertThat(BufferCalculationService.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .doesNotContain("calculateTitrationCurve", "calculatePolyproticBuffer", "applyActivityCoefficientCorrection");
    }

    private static AmountOfSubstance amount(String value) {
        return AmountOfSubstance.of(value, AmountOfSubstanceUnit.MOLE);
    }
}

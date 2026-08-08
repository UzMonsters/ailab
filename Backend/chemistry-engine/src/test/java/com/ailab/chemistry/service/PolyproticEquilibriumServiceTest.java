package com.ailab.chemistry.service;

import com.ailab.chemistry.api.PolyproticEquilibriumService;
import com.ailab.chemistry.domain.acidbase.DistributionFraction;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticErrorCode;
import com.ailab.chemistry.domain.acidbase.PolyproticException;
import com.ailab.chemistry.domain.acidbase.PolyproticInitialForm;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolyproticEquilibriumServiceTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private PolyproticEquilibriumService service;

    @BeforeEach
    void setUp() {
        service = new PolyproticEquilibriumServiceImpl(new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository()));
    }

    @Test
    void resolvesCarbonicCatalogueFamilyAndCalculatesBicarbonateAmphiproticSolution() {
        var result = service.calculate(new PolyproticEquilibriumRequest(
                "FAMILY-CARBONIC",
                PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                new BigDecimal("1")
        ));

        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("8.3398"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(result.getDistribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(result.getConstants()).containsKeys("Ka1", "Ka2", "Kw");
    }

    @Test
    void resolvesSulfuricCatalogueFamilyWithoutFakeFirstStageKa() {
        var result = service.calculate(new PolyproticEquilibriumRequest(
                "FAMILY-SULFURIC",
                PolyproticInitialForm.FULLY_PROTONATED_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                BigDecimal.ZERO
        ));

        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("0.9642"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(result.getConstants()).doesNotContainKey("Ka1");
        assertThat(result.getConstants()).containsEntry("Ka2", new BigDecimal("1.02e-2"));
    }

    @Test
    void distributionServiceUsesCatalogueConstantsAtRequestedPh() {
        var fractions = service.calculateDistribution("FAMILY-CARBONIC", PhValue.of("12.00"), T25);

        assertThat(fractions).extracting(DistributionFraction::speciesCode)
                .containsExactly("SPEC-H2CO3", "SPEC-HCO3-MINUS", "SPEC-CO3-2MINUS");
        assertThat(fractions.get(2).fraction()).isGreaterThan(new BigDecimal("0.97"));
    }

    @Test
    void rejectsUnsupportedSolventTemperatureMissingSpectatorAndNoDeferredApis() {
        assertThatThrownBy(() -> service.calculate(new PolyproticEquilibriumRequest(
                "FAMILY-CARBONIC", PolyproticInitialForm.FULLY_PROTONATED_ACID,
                MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), T25, "COMP-ETHANOL", null, BigDecimal.ZERO)))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.UNSUPPORTED_SOLVENT);

        assertThatThrownBy(() -> service.calculateDistribution("FAMILY-CARBONIC", PhValue.of("7.00"), Temperature.of("20.0", TemperatureUnit.CELSIUS)))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA);

        assertThatThrownBy(() -> service.calculate(new PolyproticEquilibriumRequest(
                "FAMILY-CARBONIC", PolyproticInitialForm.FULLY_DEPROTONATED_SALT,
                MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), T25, "COMP-H2O", null, BigDecimal.ZERO)))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.MISSING_SPECTATOR_ION);

        assertThat(PolyproticEquilibriumService.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .doesNotContain("calculateTitration", "applyActivityCorrection");
    }
}

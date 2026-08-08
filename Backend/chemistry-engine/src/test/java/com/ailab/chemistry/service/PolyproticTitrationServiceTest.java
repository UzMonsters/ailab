package com.ailab.chemistry.service;

import com.ailab.chemistry.api.PolyproticTitrationService;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationErrorCode;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationException;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationSystemType;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
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

class PolyproticTitrationServiceTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private PolyproticTitrationService service;

    @BeforeEach
    void setUp() {
        var referenceService = new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository());
        var equilibriumService = new PolyproticEquilibriumServiceImpl(referenceService);
        service = new PolyproticTitrationServiceImpl(referenceService, equilibriumService);
    }

    @Test
    void resolvesCarbonicCatalogueAndCalculatesCharacteristicPoints() {
        var characteristic = service.calculateCharacteristicPoints(carbonicAcidWithBase());

        assertThat(characteristic.equivalencePoints()).hasSize(2);
        assertThat(characteristic.equivalencePoints().get(0).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("25.00");
        assertThat(characteristic.equivalencePoints().get(1).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("50.00");
        assertThat(characteristic.points().get(0).ph().getValue()).isCloseTo(new BigDecimal("3.6763"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(characteristic.points().get(2).distribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(characteristic.points().get(4).distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
        assertThat(characteristic.points()).allSatisfy(point -> {
            assertThat(point.residual().massBalanceResidual()).isLessThan(new BigDecimal("1e-14"));
            assertThat(point.residual().chargeBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
        });
    }

    @Test
    void resolvesSulfuricCatalogueWithoutSyntheticFirstKa() {
        var result = service.calculatePoint(sulfuricAcidWithBase(), Volume.of("0.00", VolumeUnit.MILLILITER));

        assertThat(result.constants()).doesNotContainKey("Ka1");
        assertThat(result.constants()).containsEntry("Ka2", new BigDecimal("1.02e-2"));
        assertThat(result.distribution().getFractions().get(0).fraction()).isEqualByComparingTo("0");
    }

    @Test
    void ordersCurveVolumesAndRejectsUnsupportedRequests() {
        var curve = service.calculateCurve(carbonicAcidWithBase(), List.of(ml("30.00"), ml("0.00"), ml("10.00")));

        assertThat(curve.points()).extracting(point -> point.addedTitrantVolume().in(VolumeUnit.MILLILITER))
                .containsExactly(new BigDecimal("0.00"), new BigDecimal("10.00"), new BigDecimal("30.00"));

        assertThatThrownBy(() -> service.calculatePoint(unsupportedFamily(), ml("0.00")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.UNSUPPORTED_FAMILY);

        assertThatThrownBy(() -> service.calculatePoint(carbonicAcidWithBase("COMP-ETHANOL"), ml("0.00")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.UNSUPPORTED_SOLVENT);

        assertThatThrownBy(() -> service.calculatePoint(carbonicAcidWithBase(Temperature.of("20.0", TemperatureUnit.CELSIUS)), ml("0.00")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA);
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase() {
        return carbonicAcidWithBase(T25, "COMP-H2O");
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase(Temperature temperature) {
        return carbonicAcidWithBase(temperature, "COMP-H2O");
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase(String solvent) {
        return carbonicAcidWithBase(T25, solvent);
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase(Temperature temperature, String solvent) {
        return new PolyproticTitrationRequest(
                "FAMILY-CARBONIC",
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                temperature,
                solvent,
                null,
                "SPEC-NA-PLUS",
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest sulfuricAcidWithBase() {
        return new PolyproticTitrationRequest(
                "FAMILY-SULFURIC",
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                "SPEC-NA-PLUS",
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest unsupportedFamily() {
        return new PolyproticTitrationRequest(
                "FAMILY-OXALIC",
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                "SPEC-NA-PLUS",
                new BigDecimal("0.00001"));
    }

    private static Volume ml(String value) {
        return Volume.of(value, VolumeUnit.MILLILITER);
    }
}

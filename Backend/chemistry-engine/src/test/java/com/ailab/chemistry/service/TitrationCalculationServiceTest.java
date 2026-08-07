package com.ailab.chemistry.service;

import com.ailab.chemistry.api.TitrationCalculationService;
import com.ailab.chemistry.domain.acidbase.TitrationErrorCode;
import com.ailab.chemistry.domain.acidbase.TitrationException;
import com.ailab.chemistry.domain.acidbase.TitrationPointRequest;
import com.ailab.chemistry.domain.acidbase.TitrationRegion;
import com.ailab.chemistry.domain.acidbase.TitrationRequest;
import com.ailab.chemistry.domain.acidbase.TitrationSystemType;
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

class TitrationCalculationServiceTest {

    private TitrationCalculationService service;

    @BeforeEach
    void setUp() {
        var referenceService = new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository());
        var equilibriumService = new AcidBaseEquilibriumServiceImpl(referenceService);
        service = new TitrationCalculationServiceImpl(referenceService, equilibriumService);
    }

    @Test
    void resolvesStrongAndWeakCatalogueSystems() {
        TitrationRequest strong = request("SPEC-HCL", "SPEC-NAOH");
        assertThat(service.resolveTitrationSystem(strong).getSystemType()).isEqualTo(TitrationSystemType.STRONG_ACID_STRONG_BASE);

        TitrationRequest weakAcid = request("SPEC-CH3COOH", "SPEC-NAOH");
        assertThat(service.resolveTitrationSystem(weakAcid).getSystemType()).isEqualTo(TitrationSystemType.WEAK_ACID_STRONG_BASE);

        TitrationRequest weakBase = request("SPEC-NH3", "SPEC-HCL");
        assertThat(service.resolveTitrationSystem(weakBase).getSystemType()).isEqualTo(TitrationSystemType.WEAK_BASE_STRONG_ACID);
    }

    @Test
    void pointCalculationMatchesPhase7DInitialWeakAcidAndStrongEquivalence() {
        var weakInitial = service.calculatePoint(request("SPEC-CH3COOH", "SPEC-NAOH"), Volume.of("0", VolumeUnit.MILLILITER));
        assertThat(weakInitial.getRegion()).isEqualTo(TitrationRegion.INITIAL);
        assertThat(weakInitial.getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));

        var strongEquivalence = service.calculatePoint(request("SPEC-HCL", "SPEC-NAOH"), Volume.of("25.00", VolumeUnit.MILLILITER));
        assertThat(strongEquivalence.getRegion()).isEqualTo(TitrationRegion.EQUIVALENCE);
        assertThat(strongEquivalence.getPh().getValue()).isEqualByComparingTo("7.0000");
    }

    @Test
    void pointRequestDelegatesToServiceAndCurvePreservesRequestedPointsOrderedByVolume() {
        TitrationRequest request = request("SPEC-HCL", "SPEC-NAOH");

        assertThat(service.calculatePoint(new TitrationPointRequest(request, Volume.of("12.5", VolumeUnit.MILLILITER))).getRegion())
                .isEqualTo(TitrationRegion.PRE_EQUIVALENCE);

        var curve = service.calculateCurve(request, List.of(Volume.of("25", VolumeUnit.MILLILITER), Volume.of("0", VolumeUnit.MILLILITER)));
        assertThat(curve.getPoints()).extracting(p -> p.getAddedTitrantVolume().in(VolumeUnit.MILLILITER))
                .containsExactly(new BigDecimal("0"), new BigDecimal("25"));
    }

    @Test
    void rejectsUnsupportedPairsSolventsTemperaturesAndInvalidQuantities() {
        assertThatThrownBy(() -> service.resolveTitrationSystem(request("SPEC-CH3COOH", "SPEC-NH3")))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.UNSUPPORTED_TITRATION_SYSTEM);

        assertThatThrownBy(() -> service.resolveTitrationSystem(request("SPEC-HCO3-MINUS", "SPEC-NAOH")))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.POLYPROTIC_TITRATION_UNSUPPORTED);

        assertThatThrownBy(() -> service.resolveTitrationSystem(request("SPEC-HCL", "SPEC-NAOH", "COMP-ETHANOL", Temperature.of("25.0", TemperatureUnit.CELSIUS))))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.UNSUPPORTED_SOLVENT);

        assertThatThrownBy(() -> service.resolveTitrationSystem(request("SPEC-HCL", "SPEC-NAOH", "COMP-H2O", Temperature.of("20.0", TemperatureUnit.CELSIUS))))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA);

        assertThatThrownBy(() -> new TitrationRequest(
                "SPEC-HCL",
                "SPEC-NAOH",
                MolarConcentration.of("0", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("25", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER),
                Temperature.of("25.0", TemperatureUnit.CELSIUS),
                "COMP-H2O"
        )).isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.NON_POSITIVE_CONCENTRATION);
    }

    @Test
    void publicServiceDoesNotExposeDeferredBehavior() {
        assertThat(TitrationCalculationService.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .doesNotContain("calculateIndicatorTransition", "calculatePrecipitationTitration", "calculateRedoxTitration", "applyActivityCorrection");
    }

    private static TitrationRequest request(String analyte, String titrant) {
        return request(analyte, titrant, "COMP-H2O", Temperature.of("25.0", TemperatureUnit.CELSIUS));
    }

    private static TitrationRequest request(String analyte, String titrant, String solvent, Temperature temperature) {
        return new TitrationRequest(
                analyte,
                titrant,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                Volume.of("25.00", VolumeUnit.MILLILITER),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                temperature,
                solvent
        );
    }
}

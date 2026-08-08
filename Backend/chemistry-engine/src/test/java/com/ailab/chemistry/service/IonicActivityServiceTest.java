package com.ailab.chemistry.service;

import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionRequest;
import com.ailab.chemistry.domain.acidbase.ActivityEquilibriumSystemType;
import com.ailab.chemistry.domain.acidbase.ActivityErrorCode;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivitySolverStatus;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.acidbase.PolyproticInitialForm;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryActivityParameterSetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class IonicActivityServiceTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private IonicActivityService service;

    @BeforeEach
    void setUp() {
        var referenceService = new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository());
        service = new IonicActivityServiceImpl(
                referenceService,
                new AcidBaseEquilibriumServiceImpl(referenceService),
                new PolyproticEquilibriumServiceImpl(referenceService),
                new InMemoryActivityParameterSetRepository()
        );
    }

    @Test
    void calculatesIonicStrengthAndDaviesActivitiesThroughService() {
        var strength = service.calculateIonicStrength(List.of(
                new IonicSpeciesConcentration("SPEC-NA-PLUS", new BigDecimal("0.100"), 1),
                new IonicSpeciesConcentration("SPEC-CL-MINUS", new BigDecimal("0.100"), -1)
        ));
        var activities = service.calculateActivities(List.of(
                new IonicSpeciesConcentration("SPEC-NA-PLUS", new BigDecimal("0.100"), 1),
                new IonicSpeciesConcentration("SPEC-CL-MINUS", new BigDecimal("0.100"), -1)
        ), T25, "COMP-H2O", ActivityModel.DAVIES);

        assertThat(strength.value()).isEqualByComparingTo("0.100");
        assertThat(activities.ionicStrength().value()).isEqualByComparingTo("0.100");
        assertThat(activities.coefficientFor("SPEC-NA-PLUS").value()).isBetween(new BigDecimal("0.77"), new BigDecimal("0.79"));
    }

    @Test
    void idealModeReproducesExistingEquilibriumAndDaviesModeReportsCorrectedPh() {
        var ideal = service.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.IDEAL,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O"
        ));
        var corrected = service.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.DAVIES,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O"
        ));

        assertThat(ideal.activityPh().getValue()).isCloseTo(new BigDecimal("2.8810"), offset(new BigDecimal("0.0010")));
        assertThat(ideal.activityPh().getValue()).isEqualByComparingTo(ideal.idealPh().getValue());
        assertThat(corrected.activityPh().getValue()).isNotEqualByComparingTo(corrected.idealPh().getValue());
        assertThat(corrected.solverStatus()).isEqualTo(ActivitySolverStatus.CONVERGED);
        assertThat(corrected.iteration().iterationCount()).isGreaterThan(1);
    }

    @Test
    void correctsWeakBaseConjugateSaltsCarbonicAndSulfuricFamilies() {
        var weakBase = service.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.DAVIES,
                ActivityEquilibriumSystemType.WEAK_BASE,
                "SPEC-NH3",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O"
        ));
        var acetateSalt = service.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.DAVIES,
                ActivityEquilibriumSystemType.CONJUGATE_BASE_SALT,
                "SPEC-CH3COO-MINUS",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O"
        ));
        var bicarbonate = service.calculateEquilibrium(ActivityCorrectionRequest.forPolyprotic(
                ActivityModel.DAVIES,
                "FAMILY-CARBONIC",
                PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                BigDecimal.ONE
        ));
        var carbonate = service.calculateEquilibrium(ActivityCorrectionRequest.forPolyprotic(
                ActivityModel.DAVIES,
                "FAMILY-CARBONIC",
                PolyproticInitialForm.FULLY_DEPROTONATED_SALT,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                new BigDecimal("2")
        ));
        var sulfuric = service.calculateEquilibrium(ActivityCorrectionRequest.forPolyprotic(
                ActivityModel.DAVIES,
                "FAMILY-SULFURIC",
                PolyproticInitialForm.FULLY_PROTONATED_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                BigDecimal.ZERO
        ));

        for (var result : List.of(weakBase, acetateSalt, bicarbonate, carbonate, sulfuric)) {
            assertThat(result.solverStatus()).isEqualTo(ActivitySolverStatus.CONVERGED);
            assertThat(result.ionicStrength().value()).isLessThanOrEqualTo(new BigDecimal("0.5"));
            assertThat(result.residual().chargeBalanceResidual()).isLessThan(new BigDecimal("1e-10"));
        }
        assertThat(bicarbonate.distribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(carbonate.distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
        assertThat(sulfuric.constants()).doesNotContainKey("Ka1");
    }

    @Test
    void rejectsUnsupportedConditionsAndMissingParameterSetsExplicitly() {
        assertThatThrownBy(() -> service.calculateActivities(List.of(
                new IonicSpeciesConcentration("SPEC-NA-PLUS", new BigDecimal("0.100"), 1)
        ), T25, "COMP-ETHANOL", ActivityModel.DAVIES))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(ActivityErrorCode.UNSUPPORTED_SOLVENT);

        assertThatThrownBy(() -> service.calculateActivities(List.of(
                new IonicSpeciesConcentration("SPEC-NA-PLUS", new BigDecimal("0.100"), 1)
        ), Temperature.of("20.0", TemperatureUnit.CELSIUS), "COMP-H2O", ActivityModel.DAVIES))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(ActivityErrorCode.MISSING_PARAMETER_SET);

        assertThatThrownBy(() -> service.calculateEquilibrium(ActivityCorrectionRequest.forPolyprotic(
                ActivityModel.DAVIES,
                "FAMILY-OXALIC",
                PolyproticInitialForm.FULLY_PROTONATED_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                BigDecimal.ZERO
        )))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM);
    }
}

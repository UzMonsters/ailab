package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.CompoundDetails;
import com.ailab.chemistry.api.CompoundSummary;
import com.ailab.chemistry.api.SolubilityEquilibriumService;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionRequest;
import com.ailab.chemistry.domain.acidbase.ActivityEquilibriumSystemType;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import com.ailab.chemistry.domain.solubility.PrecipitationRequest;
import com.ailab.chemistry.domain.solubility.SaturationRequest;
import com.ailab.chemistry.domain.solubility.SaturationStatus;
import com.ailab.chemistry.domain.solubility.SolubilityErrorCode;
import com.ailab.chemistry.domain.solubility.SolubilityException;
import com.ailab.chemistry.domain.solubility.SolubilityReferenceRepository;
import com.ailab.chemistry.domain.solubility.SolutionIonAmount;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryActivityParameterSetRepository;
import com.ailab.chemistry.infrastructure.persistence.solubility.InMemorySolubilityReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class SolubilityEquilibriumServiceTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private SolubilityEquilibriumService service;
    private AcidBaseEquilibriumServiceImpl acidBaseEquilibriumService;

    @BeforeEach
    void setUp() {
        var referenceService = new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository());
        acidBaseEquilibriumService = new AcidBaseEquilibriumServiceImpl(referenceService);
        var ionicActivityService = new IonicActivityServiceImpl(
                referenceService,
                acidBaseEquilibriumService,
                new PolyproticEquilibriumServiceImpl(referenceService),
                new InMemoryActivityParameterSetRepository()
        );
        SolubilityReferenceRepository repository = new InMemorySolubilityReferenceRepository();
        service = new SolubilityEquilibriumServiceImpl(repository, ionicActivityService, compoundService());
    }

    @Test
    void idealActivityRegressionMatchesPhase7DWeakAcidWithinTolerance() {
        var phase7d = acidBaseEquilibriumService.calculateWeakAcid(
                "SPEC-CH3COOH",
                com.ailab.chemistry.domain.measurement.MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25);
        var referenceService = new AcidBaseReferenceServiceImpl(new InMemoryAcidBaseReferenceRepository());
        var ionic = new IonicActivityServiceImpl(referenceService, acidBaseEquilibriumService, new PolyproticEquilibriumServiceImpl(referenceService), new InMemoryActivityParameterSetRepository());
        var result = ionic.calculateEquilibrium(ActivityCorrectionRequest.forSpecies(
                ActivityModel.IDEAL,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                com.ailab.chemistry.domain.measurement.MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O"));

        assertThat(result.activityPh().getValue()).isCloseTo(phase7d.getPh().getValue(), offset(new BigDecimal("0.0001")));
    }

    @Test
    void calculatesSaturationAndDaviesDifferenceThroughRepository() {
        var ideal = service.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE",
                List.of(
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CA-2PLUS", new BigDecimal("0.00010"), 2),
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CO3-2MINUS", new BigDecimal("0.00010"), -2)
                ),
                List.of(),
                T25,
                "COMP-H2O",
                ActivityModel.IDEAL));
        var davies = service.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE",
                List.of(
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CA-2PLUS", new BigDecimal("0.00010"), 2),
                        new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CO3-2MINUS", new BigDecimal("0.00010"), -2)
                ),
                List.of(),
                T25,
                "COMP-H2O",
                ActivityModel.DAVIES));

        assertThat(ideal.status()).isEqualTo(SaturationStatus.SUPERSATURATED);
        assertThat(davies.ionicProduct().value()).isLessThan(ideal.ionicProduct().value());
        assertThat(davies.iterations()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void precipitatesAndUsesCatalogueMolarMassThroughService() {
        var result = service.calculatePrecipitation(PrecipitationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE",
                List.of(
                        new SolutionIonAmount("SPEC-CA-2PLUS", AmountOfSubstance.of("0.0010", AmountOfSubstanceUnit.MOLE), 2),
                        new SolutionIonAmount("SPEC-CO3-2MINUS", AmountOfSubstance.of("0.0010", AmountOfSubstanceUnit.MOLE), -2)
                ),
                Volume.of("0.100", VolumeUnit.LITER),
                List.of(),
                T25,
                "COMP-H2O",
                ActivityModel.IDEAL));

        assertThat(result.initialStatus()).isEqualTo(SaturationStatus.SUPERSATURATED);
        assertThat(result.precipitatedMoles().in(AmountOfSubstanceUnit.MOLE)).isGreaterThan(new BigDecimal("0.0009"));
        assertThat(result.precipitatedMass()).isPresent();
        assertThat(result.precipitatedMass().orElseThrow().in(com.ailab.chemistry.domain.measurement.MassUnit.GRAM))
                .isCloseTo(new BigDecimal("0.0994"), offset(new BigDecimal("0.0005")));
    }

    @Test
    void rejectsMissingEquilibriumUnsupportedConditionsAndInvalidSpecies() {
        assertThatThrownBy(() -> service.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-NOT-SEEDED", List.of(), List.of(), T25, "COMP-H2O", ActivityModel.IDEAL)))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.MISSING_KSP);

        assertThatThrownBy(() -> service.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE",
                List.of(new com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration("SPEC-CA-2PLUS", new BigDecimal("0.001"), 1)),
                List.of(), T25, "COMP-H2O", ActivityModel.IDEAL)))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.INVALID_ION_SPECIES);

        assertThatThrownBy(() -> service.calculateSaturation(SaturationRequest.forEquilibriumCode(
                "KSP-CACO3-CALCITE", List.of(), List.of(), Temperature.of("20.0", TemperatureUnit.CELSIUS), "COMP-H2O", ActivityModel.IDEAL)))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS);
    }

    private static CompoundCatalogService compoundService() {
        return new CompoundCatalogService() {
            @Override
            public CompoundDetails getById(UUID compoundId) {
                throw new UnsupportedOperationException();
            }

            @Override
            public CompoundDetails getByCode(String compoundCode) {
                BigDecimal molarMass = switch (compoundCode) {
                    case "COMP-CACO3" -> new BigDecimal("100.086");
                    case "COMP-MG-OH-2" -> new BigDecimal("58.319");
                    case "COMP-AL-OH-3" -> new BigDecimal("78.003");
                    default -> throw new IllegalArgumentException("missing compound " + compoundCode);
                };
                return new CompoundDetails(UUID.nameUUIDFromBytes(compoundCode.getBytes()), compoundCode, compoundCode,
                        List.of(), compoundCode, compoundCode, compoundCode, 0, null, molarMass, null, null,
                        "TEST", "g/mol", List.of(), List.of(), "compound-core-v1.0.0", "test");
            }

            @Override
            public List<CompoundSummary> findByNormalizedFormula(String formula) {
                return List.of();
            }

            @Override
            public List<CompoundSummary> findByCompositionFormula(String formula) {
                return List.of();
            }

            @Override
            public List<CompoundSummary> searchByName(String query) {
                return List.of();
            }

            @Override
            public List<CompoundSummary> listCompounds() {
                return List.of();
            }
        };
    }

}

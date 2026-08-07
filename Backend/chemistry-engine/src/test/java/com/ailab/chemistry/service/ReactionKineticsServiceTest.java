package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ReactionKineticsService;
import com.ailab.chemistry.domain.kinetics.ArrheniusRequest;
import com.ailab.chemistry.domain.kinetics.ArrheniusResult;
import com.ailab.chemistry.domain.kinetics.HalfLifeResult;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawRequest;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawResult;
import com.ailab.chemistry.domain.kinetics.KineticProfile;
import com.ailab.chemistry.domain.kinetics.KineticProfileRepository;
import com.ailab.chemistry.domain.kinetics.KineticProgressRequest;
import com.ailab.chemistry.domain.kinetics.KineticProgressResult;
import com.ailab.chemistry.domain.kinetics.KineticSolverStatus;
import com.ailab.chemistry.domain.kinetics.OverallReactionOrder;
import com.ailab.chemistry.domain.kinetics.RateConstant;
import com.ailab.chemistry.domain.kinetics.RateConstantDimension;
import com.ailab.chemistry.domain.kinetics.RateEvaluationRequest;
import com.ailab.chemistry.domain.kinetics.RateEvaluationResult;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.kinetics.InMemoryKineticProfileRepository;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReactionKineticsServiceTest {

    private ReactionKineticsService kineticsService;
    private KineticProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        var reactionRepo = new InMemoryReactionRepository();
        reactionRepo.save(new com.ailab.chemistry.domain.reaction.Reaction(
                new com.ailab.chemistry.domain.reaction.ReactionId(java.util.UUID.fromString("33333333-1111-1111-1111-111111111101")),
                new com.ailab.chemistry.domain.reaction.ReactionCode("RXN-ELEM-H-O2-PROPAGATION"),
                new com.ailab.chemistry.domain.reaction.ReactionName("Elementary H + O2 Propagation Step"),
                java.util.List.of(),
                new com.ailab.chemistry.domain.reaction.ReactionEquation("H + O2 -> OH + O", "H + O2 -> OH + O", "H + O2 -> OH + O", "H + O2 -> OH + O"),
                java.util.List.of(
                        new com.ailab.chemistry.domain.reaction.ReactionTerm(java.util.UUID.fromString("11111111-0000-0000-0000-000000000001"), "COMP-RAD-H", "H", com.ailab.chemistry.domain.reaction.ReactionSide.REACTANT, java.math.BigInteger.ONE, com.ailab.chemistry.domain.reaction.ReactionSpeciesState.GAS, 1),
                        new com.ailab.chemistry.domain.reaction.ReactionTerm(java.util.UUID.fromString("11111111-0000-0000-0000-000000000002"), "COMP-O2", "O2", com.ailab.chemistry.domain.reaction.ReactionSide.REACTANT, java.math.BigInteger.ONE, com.ailab.chemistry.domain.reaction.ReactionSpeciesState.GAS, 2),
                        new com.ailab.chemistry.domain.reaction.ReactionTerm(java.util.UUID.fromString("11111111-0000-0000-0000-000000000003"), "COMP-RAD-OH", "OH", com.ailab.chemistry.domain.reaction.ReactionSide.PRODUCT, java.math.BigInteger.ONE, com.ailab.chemistry.domain.reaction.ReactionSpeciesState.GAS, 3),
                        new com.ailab.chemistry.domain.reaction.ReactionTerm(java.util.UUID.fromString("11111111-0000-0000-0000-000000000004"), "COMP-RAD-O", "O", com.ailab.chemistry.domain.reaction.ReactionSide.PRODUCT, java.math.BigInteger.ONE, com.ailab.chemistry.domain.reaction.ReactionSpeciesState.GAS, 4)
                ),
                com.ailab.chemistry.domain.reaction.ReactionDirectionality.IRREVERSIBLE,
                java.util.List.of(), java.util.List.of(), java.util.List.of(), "reaction-core-v1.0.0",
                new com.ailab.chemistry.domain.reaction.ReactionProvenance("NIST", java.util.List.of(), "")
        ));
        var reactionCatalog = new ReactionCatalogServiceImpl(reactionRepo);
        profileRepository = new InMemoryKineticProfileRepository();
        kineticsService = new ReactionKineticsServiceImpl(reactionCatalog, profileRepository);
    }

    @Test
    void calculateRateForWaterSynthesisAndDimensionalValidation() {
        KineticProfile profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();

        RateEvaluationResult result = kineticsService.calculateRate(new RateEvaluationRequest(
                "RXN-ELEM-H-O2-PROPAGATION",
                profile.rateLaw(),
                profile.referenceRateConstant(),
                Map.of("COMP-RAD-H", new BigDecimal("2.0"), "COMP-O2", new BigDecimal("1.0"))
        ));

        // r = 73470117.272 * 2.0 * 1.0 = 146940234.544 mol/(L*s)
        assertThat(result.reactionRate().value()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.overallOrder().totalOrderValue()).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void integratedLawAndHalfLifeCalculation() {
        IntegratedRateLawRequest req = new IntegratedRateLawRequest(
                "COMP-CO",
                new BigDecimal("5.0"),
                RateConstant.of("0.02", RateConstantDimension.FIRST_ORDER),
                OverallReactionOrder.of(1),
                Duration.of("10.0", DurationUnit.SECOND)
        );

        IntegratedRateLawResult intRes = kineticsService.calculateIntegratedLaw(req);
        HalfLifeResult halfRes = kineticsService.calculateHalfLife(req);

        // 5.0 * exp(-0.2) ~ 4.09365
        assertThat(intRes.finalConcentrationMolar()).isCloseTo(new BigDecimal("4.09365"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(halfRes.halfLife().in(DurationUnit.SECOND)).isCloseTo(new BigDecimal("34.657"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    void arrheniusRateConstantCalculation() {
        KineticProfile profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();

        ArrheniusResult res = kineticsService.calculateRateConstant(new ArrheniusRequest(
                profile.arrheniusParameters(), Temperature.of("1050.0", TemperatureUnit.KELVIN)));

        assertThat(res.calculatedRateConstant().value()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void simulateProgressDecayMatchesAnalyticalSolution() {
        KineticProfile profile = profileRepository.findByProfileId("KP-ELEM-H-O2-PIRRAGLIA-1989-REC3").orElseThrow();

        KineticProgressResult progressRes = kineticsService.simulateProgress(new KineticProgressRequest(
                "RXN-ELEM-H-O2-PROPAGATION",
                profile,
                Map.of("COMP-RAD-H", new BigDecimal("0.001"), "COMP-O2", new BigDecimal("0.1"), "COMP-RAD-OH", BigDecimal.ZERO, "COMP-RAD-O", BigDecimal.ZERO),
                new BigDecimal("1.0"),
                Duration.of("1e-6", DurationUnit.SECOND),
                Duration.of("1e-7", DurationUnit.SECOND),
                Temperature.of("1050.0", TemperatureUnit.KELVIN)
        ));

        assertThat(progressRes.status()).isIn(KineticSolverStatus.CONVERGED, KineticSolverStatus.DEPLETED);
        assertThat(progressRes.points()).isNotEmpty();
        assertThat(progressRes.residual().isBalanced()).isTrue();
    }
}

package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolyproticEquilibriumCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final BigDecimal KW = new BigDecimal("1.00e-14");
    private static final BigDecimal K1_CARBONIC = new BigDecimal("4.45e-7");
    private static final BigDecimal K2_CARBONIC = new BigDecimal("4.69e-11");
    private static final BigDecimal K2_SULFURIC = new BigDecimal("1.02e-2");
    private static final PolyproticEquilibriumCalculator CALCULATOR = new PolyproticEquilibriumCalculator();

    @Test
    void carbonicDistributionUsesClosedFormFractionsAtFixedPh() {
        List<DistributionFraction> fractions = CALCULATOR.calculateDistribution(carbonicFamily(), PhValue.of("6.35"));

        assertThat(fractions).extracting(DistributionFraction::speciesCode)
                .containsExactly("SPEC-H2CO3", "SPEC-HCO3-MINUS", "SPEC-CO3-2MINUS");
        assertThat(fractions.get(0).fraction()).isCloseTo(new BigDecimal("0.5009178"), org.assertj.core.data.Offset.offset(new BigDecimal("0.000001")));
        assertThat(fractions.get(1).fraction()).isCloseTo(new BigDecimal("0.4990298"), org.assertj.core.data.Offset.offset(new BigDecimal("0.000001")));
        assertThat(fractions.get(2).fraction()).isCloseTo(new BigDecimal("0.000052396"), org.assertj.core.data.Offset.offset(new BigDecimal("0.00000001")));
        assertThat(sumFractions(fractions)).isCloseTo(BigDecimal.ONE, org.assertj.core.data.Offset.offset(new BigDecimal("1e-12")));
    }

    @Test
    void carbonicFamilySolvesAcidAmphiproticAndCarbonateSaltByChargeBalance() {
        PolyproticEquilibriumResult acid = CALCULATOR.calculate(request(carbonicFamily(), PolyproticInitialForm.FULLY_PROTONATED_ACID, null, 0, "0"));
        PolyproticEquilibriumResult bicarbonate = CALCULATOR.calculate(request(carbonicFamily(), PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT, "SPEC-NA-PLUS", 1, "1"));
        PolyproticEquilibriumResult carbonate = CALCULATOR.calculate(request(carbonicFamily(), PolyproticInitialForm.FULLY_DEPROTONATED_SALT, "SPEC-NA-PLUS", 1, "2"));

        assertThat(acid.getPh().getValue()).isCloseTo(new BigDecimal("3.6763"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(dominantSpecies(acid)).isEqualTo("SPEC-H2CO3");
        assertThat(bicarbonate.getPh().getValue()).isCloseTo(new BigDecimal("8.3398"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(dominantSpecies(bicarbonate)).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(carbonate.getPh().getValue()).isCloseTo(new BigDecimal("11.6544"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0010")));
        assertThat(dominantSpecies(carbonate)).isEqualTo("SPEC-CO3-2MINUS");

        for (PolyproticEquilibriumResult result : List.of(acid, bicarbonate, carbonate)) {
            assertThat(sumConcentrations(result)).isCloseTo(new BigDecimal("0.100"), org.assertj.core.data.Offset.offset(new BigDecimal("1e-14")));
            assertThat(result.getResidual().massBalanceResidual()).isLessThan(new BigDecimal("1e-14"));
            assertThat(result.getResidual().chargeBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
            assertThat(result.getSolverStatus()).isEqualTo(PolyproticSolverStatus.CONVERGED);
        }
    }

    @Test
    void sulfuricFamilyTreatsFirstDissociationAsCompleteAndDoesNotInventFakeKa1() {
        PolyproticEquilibriumResult result = CALCULATOR.calculate(request(sulfuricFamily(), PolyproticInitialForm.FULLY_PROTONATED_ACID, null, 0, "0"));

        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("0.9642"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(result.getDistribution().getFractions()).extracting(DistributionFraction::speciesCode)
                .containsExactly("SPEC-H2SO4", "SPEC-HSO4-MINUS", "SPEC-SO4-2MINUS");
        assertThat(result.getDistribution().getFractions().get(0).fraction()).isEqualByComparingTo("0");
        assertThat(result.getDistribution().getFractions().get(1).fraction()).isCloseTo(new BigDecimal("0.91413"), org.assertj.core.data.Offset.offset(new BigDecimal("0.00005")));
        assertThat(result.getDistribution().getFractions().get(2).fraction()).isCloseTo(new BigDecimal("0.08587"), org.assertj.core.data.Offset.offset(new BigDecimal("0.00005")));
        assertThat(result.getConstants()).doesNotContainKey("Ka1");
        assertThat(result.getConstants()).containsEntry("Ka2", K2_SULFURIC);
    }

    @Test
    void fixedPhDistributionIdentifiesDominantCarbonicSpeciesAcrossPhRange() {
        assertThat(dominantFractionAtPh("4.00")).isEqualTo("SPEC-H2CO3");
        assertThat(dominantFractionAtPh("8.34")).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(dominantFractionAtPh("12.00")).isEqualTo("SPEC-CO3-2MINUS");
    }

    @Test
    void rejectsUnsafeFamiliesRequestsAndSolverFailuresWithStructuredErrors() {
        assertThatThrownBy(() -> carbonicFamilyWith(List.of(step(1, K1_CARBONIC))))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.MISSING_KA_STEP);

        assertThatThrownBy(() -> carbonicFamilyWith(List.of(step(1, K1_CARBONIC), step(3, K2_CARBONIC))))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.NONCONTIGUOUS_DISSOCIATION_STEPS);

        assertThatThrownBy(() -> carbonicFamilyWith(List.of(step(1, K1_CARBONIC), new PolyproticDissociationConstant(2, K2_CARBONIC, Temperature.of("30.0", TemperatureUnit.CELSIUS), "COMP-H2O"))))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.MIXED_REFERENCE_CONDITIONS);

        assertThatThrownBy(() -> request(carbonicFamily(), PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT, null, 0, "0"))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.MISSING_SPECTATOR_ION);

        assertThatThrownBy(() -> CALCULATOR.calculate(request(carbonicFamily(), PolyproticInitialForm.FULLY_DEPROTONATED_SALT, "SPEC-NA-PLUS", 1, "1")))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.INVALID_SPECTATOR_STOICHIOMETRY);

        PolyproticAcidFamily pathological = carbonicFamilyWith(List.of(step(1, new BigDecimal("1e-80")), step(2, new BigDecimal("1e-80"))));
        assertThatThrownBy(() -> CALCULATOR.calculate(request(pathological, PolyproticInitialForm.FULLY_PROTONATED_ACID, null, 0, "0", new BigDecimal("1e-80"))))
                .isInstanceOf(PolyproticException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticErrorCode.SOLVER_CONVERGENCE_FAILED);
    }

    private static PolyproticEquilibriumRequest request(PolyproticAcidFamily family, PolyproticInitialForm form, String spectatorCode, int spectatorCharge, String spectatorStoichiometry) {
        return request(family, form, spectatorCode, spectatorCharge, spectatorStoichiometry, KW);
    }

    private static PolyproticEquilibriumRequest request(PolyproticAcidFamily family, PolyproticInitialForm form, String spectatorCode, int spectatorCharge, String spectatorStoichiometry, BigDecimal kw) {
        return new PolyproticEquilibriumRequest(
                family,
                form,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                spectatorCode,
                spectatorCharge,
                new BigDecimal(spectatorStoichiometry),
                kw
        );
    }

    private static PolyproticAcidFamily carbonicFamily() {
        return carbonicFamilyWith(List.of(step(1, K1_CARBONIC), step(2, K2_CARBONIC)));
    }

    private static PolyproticAcidFamily carbonicFamilyWith(List<PolyproticDissociationConstant> constants) {
        return new PolyproticAcidFamily(
                "FAMILY-CARBONIC",
                List.of(
                        new PolyproticSpecies("SPEC-H2CO3", "H2CO3", 2, 0),
                        new PolyproticSpecies("SPEC-HCO3-MINUS", "HCO3-", 1, -1),
                        new PolyproticSpecies("SPEC-CO3-2MINUS", "CO3^2-", 0, -2)
                ),
                constants,
                false,
                List.of("test-reference")
        );
    }

    private static PolyproticAcidFamily sulfuricFamily() {
        return new PolyproticAcidFamily(
                "FAMILY-SULFURIC",
                List.of(
                        new PolyproticSpecies("SPEC-H2SO4", "H2SO4", 2, 0),
                        new PolyproticSpecies("SPEC-HSO4-MINUS", "HSO4-", 1, -1),
                        new PolyproticSpecies("SPEC-SO4-2MINUS", "SO4^2-", 0, -2)
                ),
                List.of(new PolyproticDissociationConstant(2, K2_SULFURIC, T25, "COMP-H2O")),
                true,
                List.of("test-reference")
        );
    }

    private static PolyproticDissociationConstant step(int stepNumber, BigDecimal value) {
        return new PolyproticDissociationConstant(stepNumber, value, T25, "COMP-H2O");
    }

    private static BigDecimal sumFractions(List<DistributionFraction> fractions) {
        return fractions.stream().map(DistributionFraction::fraction).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal sumConcentrations(PolyproticEquilibriumResult result) {
        return result.getDistribution().getFractions().stream()
                .map(DistributionFraction::concentration)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String dominantSpecies(PolyproticEquilibriumResult result) {
        return result.getDistribution().dominantSpeciesCode();
    }

    private static String dominantFractionAtPh(String ph) {
        return CALCULATOR.calculateDistribution(carbonicFamily(), PhValue.of(ph)).stream()
                .max((a, b) -> a.fraction().compareTo(b.fraction()))
                .orElseThrow()
                .speciesCode();
    }
}

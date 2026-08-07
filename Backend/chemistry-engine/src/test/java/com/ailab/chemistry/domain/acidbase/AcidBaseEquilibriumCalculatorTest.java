package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AcidBaseEquilibriumCalculatorTest {

    private AcidBaseEquilibriumCalculator calculator;
    private BigDecimal kw;

    @BeforeEach
    void setUp() {
        calculator = new AcidBaseEquilibriumCalculator();
        kw = new BigDecimal("1.00e-14");
    }

    @Test
    @DisplayName("1. Pure water equilibrium: [H3O+] = [OH-] = 1e-7 M, pH = pOH = 7.0000")
    void testPureWaterEquilibrium() {
        AcidBaseEquilibriumResult result = calculator.calculatePureWater(kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.PURE_WATER);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));
        assertThat(result.getPoh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));
        assertThat(result.getHydroniumConcentration().getValue()).isEqualByComparingTo(new BigDecimal("1.0E-7"));
        assertThat(result.getHydroxideConcentration().getValue()).isEqualByComparingTo(new BigDecimal("1.0E-7"));
        assertThat(result.getSolverStatus()).isEqualTo(SolverStatus.CONVERGED);
    }

    @Test
    @DisplayName("2a. Strong acid (0.1 M HCl): pH = 1.0000, pOH = 13.0000")
    void testStrongAcidStandard() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        AcidBaseEquilibriumResult result = calculator.calculateStrongAcid(conc, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.STRONG_ACID);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("1.0000"));
        assertThat(result.getPoh().getValue()).isEqualByComparingTo(new BigDecimal("13.0000"));
        assertThat(result.getResidual().getValue()).isLessThan(new BigDecimal("1e-10"));
        assertThat(result.getSolverStatus()).isEqualTo(SolverStatus.CONVERGED);
    }

    @Test
    @DisplayName("2b. Very dilute strong acid (1e-8 M HCl): water autoionization dominates, pH ~ 6.9781")
    void testDiluteStrongAcidWaterAutoionization() {
        MolarConcentration conc = MolarConcentration.of("1.0e-8", MolarConcentrationUnit.MOL_PER_LITER);
        AcidBaseEquilibriumResult result = calculator.calculateStrongAcid(conc, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.STRONG_ACID);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("6.9781"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
        assertThat(result.getHydroniumConcentration().getValue()).isGreaterThan(new BigDecimal("1.0e-8"));
    }

    @Test
    @DisplayName("3. Strong base (0.1 M NaOH): pH = 13.0000, pOH = 1.0000")
    void testStrongBase() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        AcidBaseEquilibriumResult result = calculator.calculateStrongBase(conc, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.STRONG_BASE);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("13.0000"));
        assertThat(result.getPoh().getValue()).isEqualByComparingTo(new BigDecimal("1.0000"));
    }

    @Test
    @DisplayName("4. Weak acid (0.1 M Acetic Acid, Ka = 1.75e-5): pH ~ 2.879")
    void testWeakAcidAcetic() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal ka = new BigDecimal("1.75e-5");
        AcidBaseEquilibriumResult result = calculator.calculateWeakAcid(AcidBaseSystemType.WEAK_ACID, conc, ka, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.WEAK_ACID);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
        assertThat(result.getResidual().getValue()).isLessThan(new BigDecimal("1e-10"));
    }

    @Test
    @DisplayName("5. Weak base (0.1 M Ammonia, Kb = 1.76e-5): pH ~ 11.124")
    void testWeakBaseAmmonia() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal kb = new BigDecimal("1.76e-5");
        AcidBaseEquilibriumResult result = calculator.calculateWeakBase(AcidBaseSystemType.WEAK_BASE, conc, kb, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.WEAK_BASE);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("11.124"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
        assertThat(result.getResidual().getValue()).isLessThan(new BigDecimal("1e-10"));
    }

    @Test
    @DisplayName("6. Conjugate-acid salt (0.1 M Ammonium, Ka = 5.69e-10): pH ~ 5.122")
    void testConjugateAcidSalt() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal ka = new BigDecimal("5.69e-10");
        AcidBaseEquilibriumResult result = calculator.calculateWeakAcid(AcidBaseSystemType.CONJUGATE_ACID_SALT, conc, ka, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.CONJUGATE_ACID_SALT);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("5.122"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("7. Conjugate-base salt (0.1 M Acetate, Kb = 5.71e-10): pH ~ 8.878")
    void testConjugateBaseSalt() {
        MolarConcentration conc = MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal kb = new BigDecimal("5.71e-10");
        AcidBaseEquilibriumResult result = calculator.calculateWeakBase(AcidBaseSystemType.CONJUGATE_BASE_SALT, conc, kb, kw);

        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.CONJUGATE_BASE_SALT);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("8.878"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("8. Extreme concentration (2.0 M HCl): negative pH supported (pH < 0)")
    void testConcentratedStrongAcidNegativePh() {
        MolarConcentration conc = MolarConcentration.of("2.0", MolarConcentrationUnit.MOL_PER_LITER);
        AcidBaseEquilibriumResult result = calculator.calculateStrongAcid(conc, kw);

        assertThat(result.getPh().getValue()).isLessThan(BigDecimal.ZERO);
    }
}

package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryAcidBaseReferenceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AcidBaseEquilibriumServiceTest {

    private AcidBaseEquilibriumService equilibriumService;
    private Temperature t25;

    @BeforeEach
    void setUp() {
        InMemoryAcidBaseReferenceRepository referenceRepository = new InMemoryAcidBaseReferenceRepository();
        AcidBaseReferenceService referenceService = new AcidBaseReferenceServiceImpl(referenceRepository);
        equilibriumService = new AcidBaseEquilibriumServiceImpl(referenceService);
        t25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    }

    @Test
    @DisplayName("Calculate pure water equilibrium at 25 C")
    void testPureWater() {
        AcidBaseEquilibriumResult result = equilibriumService.calculatePureWater(t25);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));
        assertThat(result.getPoh().getValue()).isEqualByComparingTo(new BigDecimal("7.0000"));
    }

    @Test
    @DisplayName("Calculate 0.1 M HCl strong acid equilibrium")
    void testStrongAcid() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateStrongAcid("SPEC-HCL", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("1.0000"));
    }

    @Test
    @DisplayName("Calculate 0.1 M NaOH strong base equilibrium")
    void testStrongBaseNaOH() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateStrongBase("SPEC-NAOH", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getPh().getValue()).isEqualByComparingTo(new BigDecimal("13.0000"));
        assertThat(result.getPoh().getValue()).isEqualByComparingTo(new BigDecimal("1.0000"));
    }

    @Test
    @DisplayName("Calculate 0.1 M Acetic Acid weak acid equilibrium")
    void testWeakAcid() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateWeakAcid("SPEC-CH3COOH", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
    }

    @Test
    @DisplayName("Calculate 0.1 M Ammonia weak base equilibrium")
    void testWeakBase() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateWeakBase("SPEC-NH3", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("11.124"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
    }

    @Test
    @DisplayName("Calculate salt hydrolysis for ammonium ion (SPEC-NH4-PLUS)")
    void testSaltHydrolysisAmmonium() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateSaltHydrolysis("SPEC-NH4-PLUS", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.CONJUGATE_ACID_SALT);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("5.122"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("Calculate salt hydrolysis for acetate ion (SPEC-CH3COO-MINUS)")
    void testSaltHydrolysisAcetate() {
        AcidBaseEquilibriumResult result = equilibriumService.calculateSaltHydrolysis("SPEC-CH3COO-MINUS", MolarConcentration.of("0.1", MolarConcentrationUnit.MOL_PER_LITER), t25);
        assertThat(result.getSystemType()).isEqualTo(AcidBaseSystemType.CONJUGATE_BASE_SALT);
        assertThat(result.getPh().getValue()).isCloseTo(new BigDecimal("8.878"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("Unsupported temperature without Kw constant throws AcidBaseCalculationException")
    void testUnsupportedTemperature() {
        Temperature t100 = Temperature.of("100.0", TemperatureUnit.CELSIUS);
        assertThatThrownBy(() -> equilibriumService.calculatePureWater(t100))
                .isInstanceOf(AcidBaseCalculationException.class)
                .extracting("errorCode").isEqualTo(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT);
    }
}

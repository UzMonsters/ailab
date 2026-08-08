package com.ailab.chemistry.domain.acidbase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AcidBaseReferenceDomainTest {

    @Test
    @DisplayName("ChemicalSpecies formula validation and net charge check")
    void testChemicalSpeciesValidation() {
        ChemicalSpecies species = new ChemicalSpecies(
                new ChemicalSpeciesCode("SPEC-NH4-PLUS"),
                "Ammonium",
                "NH4+",
                SpeciesKind.CATION,
                SpeciesCharge.PLUS_ONE,
                AcidBaseRole.ACID,
                DissociationBehavior.WEAK_ELECTROLYTE,
                null
        );

        assertThat(species.getCode().getValue()).isEqualTo("SPEC-NH4-PLUS");
        assertThat(species.getCharge().getValue()).isEqualTo(1);
        assertThat(species.getPrimaryRole()).isEqualTo(AcidBaseRole.ACID);
        assertThat(species.getDissociationBehavior()).isEqualTo(DissociationBehavior.WEAK_ELECTROLYTE);

        // Mismatched charge should fail
        assertThatThrownBy(() -> new ChemicalSpecies(
                new ChemicalSpeciesCode("SPEC-NH4-INVALID"),
                "Ammonium Mismatched",
                "NH4+",
                SpeciesKind.CATION,
                SpeciesCharge.ZERO,
                AcidBaseRole.ACID,
                DissociationBehavior.WEAK_ELECTROLYTE,
                null
        )).isInstanceOf(AcidBaseException.class)
                .extracting("errorCode").isEqualTo(AcidBaseErrorCode.CHARGE_MISMATCH);
    }

    @Test
    @DisplayName("ConjugatePair validation: charge difference of exactly +1 and matching non-H element counts")
    void testConjugatePairValidation() {
        ChemicalSpecies acid = new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CH3COOH"), "Acetic Acid", "CH3COOH", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, null);
        ChemicalSpecies base = new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CH3COO-MINUS"), "Acetate", "CH3COO-", SpeciesKind.ANION, SpeciesCharge.MINUS_ONE, AcidBaseRole.BASE, null);

        // Valid conjugate pair
        ConjugatePair.validateProtonDifference(acid, base);

        // Mismatched proton difference should fail
        ChemicalSpecies invalidBase = new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-CO3-2MINUS"), "Carbonate", "CO3^2-", SpeciesKind.ANION, SpeciesCharge.of(-2), AcidBaseRole.BASE, null);
        assertThatThrownBy(() -> ConjugatePair.validateProtonDifference(acid, invalidBase))
                .isInstanceOf(AcidBaseException.class)
                .extracting("errorCode").isEqualTo(AcidBaseErrorCode.INVALID_PROTON_DIFFERENCE);
    }

    @Test
    @DisplayName("Ka/Kb positivity and pKa/pKb derivation from -log10(K)")
    void testEquilibriumConstantDerivation() {
        EquilibriumReferenceConditions cond = EquilibriumReferenceConditions.STANDARD_WATER_25C;
        // Acetic acid Ka = 1.75e-5 -> pKa = -log10(1.75e-5) = ~4.7567
        EquilibriumConstant ka = EquilibriumConstant.weak("SPEC-CH3COOH", EquilibriumConstantType.KA, 1, new BigDecimal("1.75e-5"), cond);

        assertThat(ka.getValue()).isEqualByComparingTo(new BigDecimal("1.75e-5"));
        assertThat(ka.getPValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));

        // Negative K value must be rejected
        assertThatThrownBy(() -> EquilibriumConstant.weak("SPEC-TEST", EquilibriumConstantType.KA, 1, new BigDecimal("-1.0"), cond))
                .isInstanceOf(AcidBaseException.class)
                .extracting("errorCode").isEqualTo(AcidBaseErrorCode.INVALID_EQUILIBRIUM_CONSTANT);
    }

    @Test
    @DisplayName("Strong-electrolyte policy: represented explicitly on ChemicalSpecies role and dissociation behavior")
    void testStrongElectrolytePolicy() {
        ChemicalSpecies hcl = new ChemicalSpecies(new ChemicalSpeciesCode("SPEC-HCL"), "Hydrochloric Acid", "HCl", SpeciesKind.NEUTRAL_COMPOUND, SpeciesCharge.ZERO, AcidBaseRole.ACID, DissociationBehavior.STRONG_ELECTROLYTE, "COMP-HCL");

        assertThat(hcl.getPrimaryRole()).isEqualTo(AcidBaseRole.ACID);
        assertThat(hcl.getDissociationBehavior()).isEqualTo(DissociationBehavior.STRONG_ELECTROLYTE);
    }
}

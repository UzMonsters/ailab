package com.ailab.chemistry.element;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import com.ailab.chemistry.domain.element.StandardState;
import com.ailab.chemistry.domain.element.property.*;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;

import static org.assertj.core.api.Assertions.*;

class ElementPropertyTests {

    private final PropertyProvenance testProvenance = PropertyProvenance.defaultProvenance("TEST-SRC", "Test Source Title");

    @Test
    void testLengthConversionsAndValidation() {
        Length length1 = Length.of("1000", LengthUnit.PICOMETER);
        assertThat(length1.in(LengthUnit.NANOMETER)).isEqualByComparingTo("1");
        assertThat(length1.in(LengthUnit.METER)).isEqualByComparingTo("0.000000001");

        Length length2 = Length.of("1", LengthUnit.NANOMETER);
        assertThat(length1).isEqualTo(length2);
        assertThat(length1.hashCode()).isEqualTo(length2.hashCode());

        assertThatThrownBy(() -> Length.of("-1", LengthUnit.PICOMETER))
                .isInstanceOf(NegativeQuantityException.class);
    }

    @Test
    void testDensityConversionsAndValidation() {
        Density density1 = Density.of("1", DensityUnit.GRAM_PER_CUBIC_CENTIMETER);
        assertThat(density1.in(DensityUnit.KILOGRAM_PER_CUBIC_METER)).isEqualByComparingTo("1000");

        assertThatThrownBy(() -> Density.of("0", DensityUnit.GRAM_PER_CUBIC_CENTIMETER))
                .isInstanceOf(NegativeQuantityException.class);
        assertThatThrownBy(() -> Density.of("-5", DensityUnit.KILOGRAM_PER_CUBIC_METER))
                .isInstanceOf(NegativeQuantityException.class);
    }

    @Test
    void testValencyInvariants() {
        Valency v1 = new Valency(2, true, ScientificEvidenceStatus.EVALUATED, testProvenance);
        Valency v2 = new Valency(4, true, ScientificEvidenceStatus.EVALUATED, testProvenance);

        assertThat(v1.getValency()).isEqualTo(2);
        assertThat(v1.compareTo(v2)).isLessThan(0);

        assertThatThrownBy(() -> new Valency(-1, true, ScientificEvidenceStatus.EVALUATED, testProvenance))
                .isInstanceOf(ElementPropertyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementPropertyErrorCode.INVALID_VALENCY);
    }

    @Test
    void testOxidationStateInvariants() {
        OxidationState osNeg = new OxidationState(-2, true, false, false, ScientificEvidenceStatus.EVALUATED, testProvenance);
        OxidationState osZero = new OxidationState(0, false, false, false, ScientificEvidenceStatus.EVALUATED, testProvenance);
        OxidationState osPos = new OxidationState(4, true, false, false, ScientificEvidenceStatus.EVALUATED, testProvenance);

        assertThat(osNeg.getState()).isEqualTo(-2);
        assertThat(osNeg.compareTo(osPos)).isLessThan(0);
    }

    @Test
    void testElectronegativityInvariants() {
        Electronegativity en = new Electronegativity(new BigDecimal("2.55"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, testProvenance);
        assertThat(en.getScale()).isEqualTo(ElectronegativityScale.PAULING);
        assertThat(en.getValue()).isEqualByComparingTo("2.55");

        assertThatThrownBy(() -> new Electronegativity(new BigDecimal("-1.0"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, testProvenance))
                .isInstanceOf(ElementPropertyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementPropertyErrorCode.INVALID_ELECTRONEGATIVITY);
    }

    @Test
    void testRadiusInvariants() {
        ElementRadius atomicRadius = new ElementRadius(
                RadiusKind.EMPIRICAL_ATOMIC,
                Length.of("70", LengthUnit.PICOMETER),
                null,
                ScientificEvidenceStatus.EVALUATED,
                testProvenance
        );
        assertThat(atomicRadius.getKind()).isEqualTo(RadiusKind.EMPIRICAL_ATOMIC);

        // Ionic radius requires ionic context
        assertThatThrownBy(() -> new ElementRadius(
                RadiusKind.IONIC,
                Length.of("100", LengthUnit.PICOMETER),
                null,
                ScientificEvidenceStatus.EVALUATED,
                testProvenance
        )).isInstanceOf(ElementPropertyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementPropertyErrorCode.INVALID_IONIC_RADIUS_CONTEXT);

        // Non-zero ionic charge required
        assertThatThrownBy(() -> new IonicRadiusContext(0, 6, ElectronSpinState.NOT_APPLICABLE))
                .isInstanceOf(ElementPropertyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementPropertyErrorCode.INVALID_IONIC_RADIUS_CONTEXT);
    }

    @Test
    void testDuplicateValencyRejectionInProfile() {
        Valency v1 = new Valency(2, true, ScientificEvidenceStatus.EVALUATED, testProvenance);
        Valency v2 = new Valency(2, false, ScientificEvidenceStatus.EVALUATED, testProvenance);

        assertThatThrownBy(() -> new ElementPropertyProfile(
                6, "C", PropertyDatasetVersion.V1_0_0,
                List.of(v1, v2),
                List.of(), List.of(), List.of(), null, null
        )).isInstanceOf(ElementPropertyException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementPropertyErrorCode.DUPLICATE_VALENCY);
    }
}

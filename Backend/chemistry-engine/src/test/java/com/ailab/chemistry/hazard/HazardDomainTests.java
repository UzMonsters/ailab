package com.ailab.chemistry.hazard;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.hazard.*;
import com.ailab.chemistry.infrastructure.persistence.hazard.InMemoryHazardProfileRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HazardDomainTests {

    private final TestElementMassProvider massProvider = new TestElementMassProvider();
    private final HazardProfileRepository repository = new InMemoryHazardProfileRepository(massProvider);

    @Test
    void testAvailabilityCountsAndRecalculation() {
        List<HazardProfile> allProfiles = repository.findAll();
        assertThat(allProfiles).hasSize(55);

        long classifiedCount = allProfiles.stream()
                .filter(p -> p.getAvailabilityMap().get("EU_CLP") == HazardAvailability.CLASSIFIED)
                .count();
        assertThat(classifiedCount).isEqualTo(6); // 6 active seed profiles with compound-level classifications

        long notClassifiedBySourceCount = allProfiles.stream()
                .filter(p -> p.getAvailabilityMap().get("EU_CLP") == HazardAvailability.NOT_CLASSIFIED_BY_SOURCE)
                .count();
        assertThat(notClassifiedBySourceCount).isEqualTo(3); // H2O, NaCl, C6H12O6

        long notIncludedCount = allProfiles.stream()
                .filter(p -> p.getAvailabilityMap().get("EU_CLP") == HazardAvailability.NOT_INCLUDED_IN_DATASET)
                .count();
        assertThat(notIncludedCount).isEqualTo(46); // Remaining compounds
    }

    @Test
    void testFrameworkVersusCompoundEvidence() {
        HazardProfile h2 = repository.findByCompoundCode("COMP-H2").orElseThrow();
        assertThat(h2.getSourceDocuments()).contains(KnownHazardRegistry.ECHA_CL_DOC);
        assertThat(h2.getClassifications().get(0).getSourceDocument().getJurisdiction()).isEqualTo(HazardJurisdiction.EU);

        // UN GHS framework doc has INTERNATIONAL_REFERENCE scope
        assertThat(KnownHazardRegistry.UN_GHS_FRAMEWORK_DOC.getJurisdiction()).isEqualTo(HazardJurisdiction.INTERNATIONAL_REFERENCE);
    }

    @Test
    void testScopeSensitivityGasVsSolution() {
        HazardProfile hcl = repository.findByCompoundCode("COMP-HCL").orElseThrow();
        HazardScope hclScope = hcl.getClassifications().get(0).getScope();

        assertThat(hclScope.getPhysicalForm()).isEqualTo(PhysicalForm.SOLUTION);
        assertThat(hclScope.getMinConcentration()).isEqualTo(new BigDecimal("25.0"));
        assertThat(hclScope.getMaxConcentration()).isEqualTo(new BigDecimal("37.0"));
    }

    @Test
    void testSummaryFlagTraceabilityAndNegativeTest() {
        HazardProfile h2 = repository.findByCompoundCode("COMP-H2").orElseThrow();
        HazardExplanation explanation = HazardSummaryDerivationEngine.explain(
                "COMP-H2",
                HazardSummaryFlag.FLAMMABLE,
                h2.getClassifications(),
                h2.getSupplementalHazards()
        );
        assertThat(explanation.getSummaryFlag()).isEqualTo(HazardSummaryFlag.FLAMMABLE);
        assertThat(explanation.getDetailedClassifications()).isNotEmpty();

        // Negative test: water has no flammable evidence -> explanation throws exception
        HazardProfile water = repository.findByCompoundCode("COMP-H2O").orElseThrow();
        assertThatThrownBy(() -> HazardSummaryDerivationEngine.explain("COMP-H2O", HazardSummaryFlag.FLAMMABLE, water.getClassifications(), water.getSupplementalHazards()))
                .isInstanceOf(HazardException.class)
                .hasMessageContaining("Summary flag FLAMMABLE is not supported by detailed evidence");
    }
}

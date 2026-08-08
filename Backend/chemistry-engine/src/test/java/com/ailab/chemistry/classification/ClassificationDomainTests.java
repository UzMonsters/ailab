package com.ailab.chemistry.classification;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.classification.*;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.infrastructure.persistence.classification.InMemoryClassificationProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClassificationDomainTests {

    private final TestElementMassProvider massProvider = new TestElementMassProvider();
    private final ClassificationProfileRepository profileRepository = new InMemoryClassificationProfileRepository(massProvider);

    @Test
    void testTaxonomyDefinitionCountAndHierarchy() {
        ClassificationTaxonomy taxonomy = KnownClassificationRegistry.TAXONOMY;
        assertThat(taxonomy.getDefinitions()).hasSize(41);
        assertThat(taxonomy.getVersion().getVersion()).isEqualTo("chemical-classification-v1.0.0");

        // Verify parent relationship
        ClassificationDefinition oxyacidDef = taxonomy.findDefinition(new ClassificationCode("OXYACID")).orElseThrow();
        assertThat(oxyacidDef.getParentCode()).isEqualTo(new ClassificationCode("ACID"));
    }

    @Test
    void testTaxonomyRejectsHierarchyCycle() {
        ClassificationDefinition defA = new ClassificationDefinition(new ClassificationCode("CODE_A"), ClassificationDimension.SUBSTANCE_DOMAIN, "A", "Desc A", 1, new ClassificationCode("CODE_B"));
        ClassificationDefinition defB = new ClassificationDefinition(new ClassificationCode("CODE_B"), ClassificationDimension.SUBSTANCE_DOMAIN, "B", "Desc B", 2, new ClassificationCode("CODE_A"));

        assertThatThrownBy(() -> new ClassificationTaxonomy(KnownClassificationRegistry.VERSION, List.of(defA, defB)))
                .isInstanceOf(ClassificationException.class)
                .hasMessageContaining("Hierarchy cycle detected");
    }

    @Test
    void testProfileRequiresExactlyOneSubstanceDomain() {
        CompoundId cid = new CompoundId(UUID.randomUUID());
        ClassificationAssignment patternAssign = ClassificationAssignment.derived(
                new ClassificationCode("NEUTRAL_SPECIES"),
                ClassificationDimension.COMPOSITION_PATTERN,
                ClassificationRuleCode.RULE_NET_CHARGE,
                "Note"
        );

        // Zero substance domain -> fails
        assertThatThrownBy(() -> new ClassificationProfile(cid, KnownClassificationRegistry.VERSION, List.of(patternAssign)))
                .isInstanceOf(ClassificationException.class)
                .hasMessageContaining("Exactly one SUBSTANCE_DOMAIN assignment required");
    }

    @Test
    void testProfileRejectsDuplicateAssignmentCodes() {
        CompoundId cid = new CompoundId(UUID.randomUUID());
        ClassificationAssignment sub1 = ClassificationAssignment.curated(
                new ClassificationCode("INORGANIC_COMPOUND"),
                ClassificationDimension.SUBSTANCE_DOMAIN,
                KnownClassificationRegistry.CRC_PROVENANCE,
                "Note 1"
        );
        ClassificationAssignment sub2 = ClassificationAssignment.curated(
                new ClassificationCode("INORGANIC_COMPOUND"),
                ClassificationDimension.SUBSTANCE_DOMAIN,
                KnownClassificationRegistry.CRC_PROVENANCE,
                "Note 2"
        );

        assertThatThrownBy(() -> new ClassificationProfile(cid, KnownClassificationRegistry.VERSION, List.of(sub1, sub2)))
                .isInstanceOf(ClassificationException.class)
                .hasMessageContaining("Duplicate assignment code in profile");
    }

    @Test
    void testIsomerDifferentiationEthanolVsDimethylEther() {
        ClassificationProfile ethanolProfile = profileRepository.findByCompoundCode("COMP-ETHANOL").orElseThrow();
        ClassificationProfile dmeProfile = profileRepository.findByCompoundCode("COMP-DIMETHYL-ETHER").orElseThrow();

        assertThat(ethanolProfile.hasClassification(new ClassificationCode("ALCOHOL"))).isTrue();
        assertThat(ethanolProfile.hasClassification(new ClassificationCode("ETHER"))).isFalse();

        assertThat(dmeProfile.hasClassification(new ClassificationCode("ETHER"))).isTrue();
        assertThat(dmeProfile.hasClassification(new ClassificationCode("ALCOHOL"))).isFalse();
    }

    @Test
    void testCuratedAssignmentRequiresProvenance() {
        assertThatThrownBy(() -> ClassificationAssignment.curated(
                new ClassificationCode("ALCOHOL"),
                ClassificationDimension.ORGANIC_FUNCTIONAL_CLASS,
                null,
                "Missing provenance"
        )).isInstanceOf(ClassificationException.class);
    }

    @Test
    void testDerivedAssignmentRequiresRuleCode() {
        assertThatThrownBy(() -> new ClassificationAssignment(
                new ClassificationCode("HYDRATE"),
                ClassificationDimension.COMPOSITION_PATTERN,
                ClassificationBasis.SAFE_RULE_DERIVED,
                ClassificationEvidenceStatus.DERIVED,
                null,
                null,
                "Missing rule code"
        )).isInstanceOf(ClassificationException.class);
    }

    @Test
    void testRepresentativeCompundProfiles55Seeded() {
        assertThat(profileRepository.count()).isEqualTo(55);
    }
}

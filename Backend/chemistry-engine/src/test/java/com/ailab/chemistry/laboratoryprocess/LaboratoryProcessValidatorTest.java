package com.ailab.chemistry.laboratoryprocess;

import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStatus;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStep;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessValidator;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessVersion;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessContainerRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessEquipmentRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessMaterialRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepDependency;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepId;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepType;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LaboratoryProcessValidatorTest {
    private final LaboratoryProcessValidator validator = new LaboratoryProcessValidator();

    @Test
    void acceptsValidLinearAndBranchingProcessDefinitions() {
        var linear = definition("PROC-LINEAR", List.of(
                step("measure", ProcessStepType.MEASURE, false, List.of()),
                step("add", ProcessStepType.ADD, false, List.of("measure")),
                step("observe", ProcessStepType.OBSERVE, false, List.of("add"))));
        var branching = definition("PROC-BRANCH", List.of(
                step("prepare", ProcessStepType.DISPENSE, false, List.of()),
                step("heat", ProcessStepType.HEAT, false, List.of("prepare")),
                step("sample", ProcessStepType.SAMPLE, true, List.of("prepare")),
                step("observe", ProcessStepType.OBSERVE, false, List.of("heat", "sample"))));

        assertThat(validator.validate(linear).valid()).isTrue();
        assertThat(validator.validate(branching).valid()).isTrue();
    }

    @Test
    void rejectsDuplicateMissingCyclicAndUnreachableSteps() {
        assertThat(validator.validate(definition("PROC-DUP", List.of(
                step("measure", ProcessStepType.MEASURE, false, List.of()),
                step("measure", ProcessStepType.ADD, false, List.of())))).errorCodes())
                .contains("DUPLICATE_STEP_ID");

        assertThat(validator.validate(definition("PROC-MISSING", List.of(
                step("add", ProcessStepType.ADD, false, List.of("missing"))))).errorCodes())
                .contains("MISSING_DEPENDENCY", "NO_INITIAL_STEP");

        assertThat(validator.validate(definition("PROC-CYCLE", List.of(
                step("a", ProcessStepType.ADD, false, List.of("b")),
                step("b", ProcessStepType.MIX, false, List.of("a"))))).errorCodes())
                .contains("CIRCULAR_DEPENDENCY", "NO_INITIAL_STEP");

        assertThat(validator.validate(definition("PROC-UNREACHABLE", List.of(
                step("start", ProcessStepType.DISPENSE, false, List.of()),
                step("orphan-parent", ProcessStepType.DISPENSE, false, List.of("orphan-child")),
                step("orphan-child", ProcessStepType.MIX, false, List.of("orphan-parent"))))).errorCodes())
                .contains("CIRCULAR_DEPENDENCY", "UNREACHABLE_STEP");
    }

    @Test
    void enforcesExplicitRequirementsUnitsDurationsAndImmutability() {
        var missingUnit = step("dispense", ProcessStepType.DISPENSE, false, List.of())
                .withMaterialRequirements(List.of(new ProcessMaterialRequirement(
                        "mat-1", "COMP-H2O", new BigDecimal("1"), "", "AQUEOUS", true, true)));
        assertThat(validator.validate(definition("PROC-UNIT", List.of(missingUnit))).errorCodes())
                .contains("MISSING_UNIT");

        assertThatThrownBy(() -> Duration.of("-1", DurationUnit.SECOND))
                .hasMessageContaining("negative");

        var heatWithoutExplicitRequirements = new LaboratoryProcessStep(
                new ProcessStepId("heat"),
                ProcessStepType.HEAT,
                false,
                Duration.of("1", DurationUnit.SECOND),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of("input-heat"),
                List.of("output-heat"));
        assertThat(validator.validate(definition("PROC-NO-INFER", List.of(heatWithoutExplicitRequirements))).errorCodes())
                .contains("MISSING_REQUIREMENT");

        var published = definition("PROC-PUBLISHED", List.of(step("observe", ProcessStepType.OBSERVE, false, List.of())))
                .withStatus(LaboratoryProcessStatus.PUBLISHED);
        assertThatThrownBy(() -> published.replaceSteps(List.of(step("other", ProcessStepType.OBSERVE, false, List.of()))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("immutable");

        var next = published.nextDraftVersion();
        assertThat(next.version()).isEqualTo(new LaboratoryProcessVersion(2));
        assertThat(next.status()).isEqualTo(LaboratoryProcessStatus.DRAFT);
        assertThat(published.version()).isEqualTo(new LaboratoryProcessVersion(1));
    }

    @Test
    void optionalStepsAreExplicitAndMandatoryStepsRemainMandatory() {
        var optional = step("sample", ProcessStepType.SAMPLE, true, List.of());
        var mandatory = step("mix", ProcessStepType.MIX, false, List.of("sample"));

        assertThat(optional.optional()).isTrue();
        assertThat(mandatory.optional()).isFalse();
        assertThat(validator.validate(definition("PROC-OPTIONAL", List.of(optional, mandatory))).valid()).isTrue();
    }

    private LaboratoryProcessDefinition definition(String code, List<LaboratoryProcessStep> steps) {
        return new LaboratoryProcessDefinition(code, new LaboratoryProcessVersion(1), LaboratoryProcessStatus.DRAFT, steps);
    }

    private LaboratoryProcessStep step(String id, ProcessStepType type, boolean optional, List<String> dependencies) {
        return new LaboratoryProcessStep(
                new ProcessStepId(id),
                type,
                optional,
                Duration.of("1", DurationUnit.SECOND),
                dependencies.stream().map(dep -> new ProcessStepDependency(new ProcessStepId(dep))).toList(),
                List.of(new ProcessMaterialRequirement("mat-" + id, "COMP-H2O", BigDecimal.ONE, "mL", "AQUEOUS", true, true)),
                List.of(new ProcessEquipmentRequirement("eq-" + id, "EQ-OHAUS-PX224-MASS", true)),
                List.of(new ProcessContainerRequirement("con-" + id, "CON-DWK-KIMAX-28014B-100-VOLUMETRIC", new BigDecimal("10"), false, "COMP-H2O", "AQUEOUS")),
                List.of(),
                List.of("input-" + id),
                List.of("output-" + id));
    }
}

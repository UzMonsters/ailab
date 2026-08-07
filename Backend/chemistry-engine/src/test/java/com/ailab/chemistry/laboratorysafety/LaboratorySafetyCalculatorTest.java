package com.ailab.chemistry.laboratorysafety;

import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyCalculator;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRule;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleId;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleType;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleVersion;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetySeverity;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyStatus;
import com.ailab.chemistry.domain.laboratorysafety.SafetyEvaluationStage;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleApplicability;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleCondition;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleProvenance;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelSelection;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationCommandId;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateVersion;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LaboratorySafetyCalculatorTest {

    private final LaboratorySafetyCalculator calculator = new LaboratorySafetyCalculator();

    @Test
    void preExecutionBlocksWhenFumeHoodNotOperating() {
        LaboratorySafetyRule rule = new LaboratorySafetyRule(
                new LaboratorySafetyRuleId("SAFE-FUME-HOOD-REQ"),
                new LaboratorySafetyRuleVersion(1),
                LaboratorySafetyRuleType.FUME_HOOD_REQUIREMENT,
                LaboratorySafetySeverity.CRITICAL,
                new SafetyRuleApplicability(SafetyEvaluationStage.PRE_EXECUTION, Set.of("STOICHIOMETRIC_REACTION"), Set.of("fumeHoodOperating")),
                SafetyRuleCondition.equalsCondition("fumeHoodOperating", "false"),
                SafetyRuleProvenance.defaultSourced("OSHA-1910-1450", "OSHA Standard"),
                true
        );

        SimulationCommand command = new SimulationCommand(
                new SimulationCommandId("cmd-1"),
                "step-1",
                "vessel-1",
                new ScientificOperationSpecification(SimulationOperationType.STOICHIOMETRIC_REACTION,
                        new ScientificModelSelection("method", "rxn-1", new ScientificModelReference("stoichiometry", "1.0.0"), List.of(), Map.of())),
                Map.of("fumeHoodOperating", "false"),
                List.of()
        );

        SimulationState state = SimulationState.initial(new SimulationSessionId("sess-1"));
        LaboratorySafetyEvaluationRequest request = LaboratorySafetyEvaluationRequest.preExecution(command, state, Map.of("fumeHoodOperating", "false"));
        LaboratorySafetyEvaluationResult result = calculator.evaluate(List.of(rule), request);

        assertThat(result.status()).isEqualTo(LaboratorySafetyStatus.BLOCKED);
        assertThat(result.violations()).hasSize(1);
    }

    @Test
    void preExecutionReturnsInsufficientDataWhenRequiredFieldMissing() {
        LaboratorySafetyRule rule = new LaboratorySafetyRule(
                new LaboratorySafetyRuleId("SAFE-FUME-HOOD-REQ"),
                new LaboratorySafetyRuleVersion(1),
                LaboratorySafetyRuleType.FUME_HOOD_REQUIREMENT,
                LaboratorySafetySeverity.CRITICAL,
                new SafetyRuleApplicability(SafetyEvaluationStage.PRE_EXECUTION, Set.of("STOICHIOMETRIC_REACTION"), Set.of("fumeHoodOperating")),
                SafetyRuleCondition.equalsCondition("fumeHoodOperating", "false"),
                SafetyRuleProvenance.defaultSourced("OSHA-1910-1450", "OSHA Standard"),
                true
        );

        SimulationCommand command = new SimulationCommand(
                new SimulationCommandId("cmd-1"),
                "step-1",
                "vessel-1",
                new ScientificOperationSpecification(SimulationOperationType.STOICHIOMETRIC_REACTION,
                        new ScientificModelSelection("method", "rxn-1", new ScientificModelReference("stoichiometry", "1.0.0"), List.of(), Map.of())),
                Map.of(),
                List.of()
        );

        SimulationState state = SimulationState.initial(new SimulationSessionId("sess-1"));
        LaboratorySafetyEvaluationRequest request = LaboratorySafetyEvaluationRequest.preExecution(command, state, Map.of());
        LaboratorySafetyEvaluationResult result = calculator.evaluate(List.of(rule), request);

        assertThat(result.status()).isEqualTo(LaboratorySafetyStatus.INSUFFICIENT_DATA);
    }

    @Test
    void inactiveRulesAreIgnoredInEvaluation() {
        LaboratorySafetyRule rule = new LaboratorySafetyRule(
                new LaboratorySafetyRuleId("SAFE-TEMP-LIMIT-GLASS"),
                new LaboratorySafetyRuleVersion(1),
                LaboratorySafetyRuleType.CONTAINER_TEMPERATURE_LIMIT,
                LaboratorySafetySeverity.CRITICAL,
                new SafetyRuleApplicability(SafetyEvaluationStage.POST_CALCULATION, Set.of("THERMAL_OPERATION"), Set.of("temperatureK")),
                SafetyRuleCondition.greaterThan("temperatureK", "773.15"),
                SafetyRuleProvenance.defaultSourced("ASTM-E438-92", "ASTM Standard"),
                false
        );

        SimulationCommand command = new SimulationCommand(
                new SimulationCommandId("cmd-1"),
                "step-1",
                "vessel-1",
                new ScientificOperationSpecification(SimulationOperationType.THERMAL_OPERATION,
                        new ScientificModelSelection("method", "profile-1", new ScientificModelReference("calorimetry", "1.0.0"), List.of(), Map.of())),
                Map.of("temperatureK", "800.0"),
                List.of()
        );

        SimulationState state = SimulationState.initial(new SimulationSessionId("sess-1"));
        LaboratorySafetyEvaluationRequest request = LaboratorySafetyEvaluationRequest.preExecution(command, state, Map.of("temperatureK", "800.0"));
        LaboratorySafetyEvaluationResult result = calculator.evaluate(List.of(rule), request);

        assertThat(result.status()).isEqualTo(LaboratorySafetyStatus.ALLOWED);
    }
}

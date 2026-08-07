package com.ailab.chemistry.simulationengine;

import com.ailab.chemistry.domain.laboratoryevent.ScientificOperationAppliedPayload;
import com.ailab.chemistry.domain.simulationengine.ConservationDimension;
import com.ailab.chemistry.domain.simulationengine.ConservationLedger;
import com.ailab.chemistry.domain.simulationengine.ConservationResidual;
import com.ailab.chemistry.domain.simulationengine.ConservationStatus;
import com.ailab.chemistry.domain.simulationengine.MaterialStateDelta;
import com.ailab.chemistry.domain.simulationengine.ScientificDatasetReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelSelection;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationInput;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationResult;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationTrace;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationCommandId;
import com.ailab.chemistry.domain.simulationengine.SimulationEngine;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionErrorCode;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.chemistry.domain.simulationengine.SimulationStateDelta;
import com.ailab.chemistry.domain.simulationengine.VesselStateDelta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulationEngineDomainTest {

    @Test
    void commandRequiresExactlyOneExplicitOperationType() {
        assertThatThrownBy(() -> new SimulationCommand(
                new SimulationCommandId("cmd-null-op"),
                "react",
                "vessel-a",
                new ScientificOperationSpecification(null, model("stoichiometry", "RXN-WATER-SYNTHESIS")),
                Map.of(),
                List.of()))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.EXPLICIT_OPERATION_REQUIRED);
    }

    @Test
    void bookkeepingMixRecordsOnlyMixingNoteAndDoesNotCreateMaterialDelta() {
        SimulationCommand command = new SimulationCommand(
                new SimulationCommandId("cmd-mix"),
                "mix",
                "vessel-a",
                new ScientificOperationSpecification(SimulationOperationType.BOOKKEEPING_MIX,
                        model("bookkeeping", "MIX-NO-CHEMISTRY")),
                Map.of("mixingNote", "contents homogenized"),
                List.of());

        ScientificOperationAppliedPayload payload = new SimulationEngine().execute(plan("PROC-MIX", 1), command).payload();

        assertThat(payload.eventType().name()).isEqualTo("BOOKKEEPING_MIX_APPLIED");
        assertThat(payload.stateDelta().vesselDeltas()).singleElement()
                .satisfies(delta -> {
                    assertThat(delta.vesselId()).isEqualTo("vessel-a");
                    assertThat(delta.mixingNote()).isEqualTo("contents homogenized");
                    assertThat(delta.materialDeltas()).isEmpty();
                });
        assertThat(payload.calculationTrace().selectedHandler()).isEqualTo("BOOKKEEPING_MIX");
        assertThat(payload.conservationLedger().residual(ConservationDimension.MATERIAL_AMOUNT).status())
                .isEqualTo(ConservationStatus.NOT_APPLICABLE);
    }

    @Test
    void scientificDeltaValidationRejectsFailedConservationAndImpossiblePhysicalState() {
        ConservationLedger failedLedger = new ConservationLedger(Map.of(
                ConservationDimension.ELEMENT_ATOMS,
                new ConservationResidual(ConservationStatus.FAILED, new BigDecimal("0.01"), BigDecimal.ZERO, "mol")));
        SimulationStateDelta delta = new SimulationStateDelta(List.of(new VesselStateDelta(
                "vessel-a",
                List.of(new MaterialStateDelta("vessel-a", "COMP-H2", new BigDecimal("-1"), "mol", "GAS")),
                "",
                new BigDecimal("-1"),
                BigDecimal.ONE,
                BigDecimal.ONE)), failedLedger);

        assertThatThrownBy(() -> new SimulationEngine().validate(delta))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED);
    }

    @Test
    void scientificOperationPayloadHashesAreDeterministicAndTraceStoredForReplay() {
        SimulationCommand command = new SimulationCommand(
                new SimulationCommandId("cmd-stoich"),
                "react",
                "vessel-a",
                new ScientificOperationSpecification(SimulationOperationType.STOICHIOMETRIC_REACTION,
                        model("stoichiometry", "RXN-WATER-SYNTHESIS")),
                Map.of("extentMol", "1.0"),
                List.of(
                        new MaterialStateDelta("vessel-a", "COMP-H2", new BigDecimal("-2.0"), "mol", "GAS"),
                        new MaterialStateDelta("vessel-a", "COMP-O2", new BigDecimal("-1.0"), "mol", "GAS"),
                        new MaterialStateDelta("vessel-a", "COMP-H2O", new BigDecimal("2.0"), "mol", "GAS")));

        ScientificOperationAppliedPayload first = new SimulationEngine().execute(plan("PROC-REACT", 1), command).payload();
        ScientificOperationAppliedPayload second = new SimulationEngine().execute(plan("PROC-REACT", 1), command).payload();

        assertThat(first.inputHash()).isEqualTo(second.inputHash());
        assertThat(first.resultHash()).isEqualTo(second.resultHash());
        assertThat(first.calculationTrace().input()).isEqualTo(new SimulationCalculationInput(Map.of("extentMol", "1.0")));
        assertThat(first.calculationTrace().result().values())
                .containsEntry("eventType", "STOICHIOMETRIC_REACTION_APPLIED")
                .containsEntry("extentMol", "1.0");
        assertThat(first.stateDelta().vesselDeltas()).singleElement()
                .extracting(VesselStateDelta::materialDeltas)
                .asList()
                .hasSize(3);
    }

    private ScientificModelSelection model(String method, String reactionOrProfileId) {
        return new ScientificModelSelection(
                method,
                reactionOrProfileId,
                new ScientificModelReference(method + "-model", "1.0.0"),
                List.of(new ScientificDatasetReference(method + "-dataset", "1.0.0")),
                Map.of("phaseAssumption", "explicit"));
    }

    private com.ailab.chemistry.domain.simulationengine.SimulationExecutionPlan plan(String processCode, int processVersion) {
        return new com.ailab.chemistry.domain.simulationengine.SimulationExecutionPlan(processCode, processVersion, "react");
    }
}

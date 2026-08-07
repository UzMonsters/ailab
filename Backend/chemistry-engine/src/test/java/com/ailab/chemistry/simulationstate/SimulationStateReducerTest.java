package com.ailab.chemistry.simulationstate;

import com.ailab.chemistry.domain.laboratoryevent.CausationId;
import com.ailab.chemistry.domain.laboratoryevent.CorrelationId;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEvent;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventId;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventPayload;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSequence;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventSource;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialMixedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialTransferredPayload;
import com.ailab.chemistry.domain.laboratoryevent.SampleTakenPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionCreatedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepCompletedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepDefinitionSnapshot;
import com.ailab.chemistry.domain.laboratoryevent.StepFailedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepSkippedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;
import com.ailab.chemistry.domain.simulationstate.ProcessStepExecutionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
import com.ailab.chemistry.domain.simulationstate.SimulationStateReducer;
import com.ailab.chemistry.domain.simulationstate.SimulationStateVersion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimulationStateReducerTest {
    private final SimulationSessionId sessionId = new SimulationSessionId("SIM-1");
    private final SimulationStateReducer reducer = new SimulationStateReducer();

    @Test
    void appliesValidSessionLifecycleWithContiguousSequencesAndVersionIncrement() {
        var state = reduce(
                created(),
                event(2, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("operator start")),
                event(3, LaboratoryEventType.SESSION_PAUSED, new SessionLifecyclePayload("pause")),
                event(4, LaboratoryEventType.SESSION_RESUMED, new SessionLifecyclePayload("resume")),
                event(5, LaboratoryEventType.SESSION_COMPLETED, new SessionLifecyclePayload("done")));

        assertThat(state.status()).isEqualTo(SimulationSessionStatus.COMPLETED);
        assertThat(state.version()).isEqualTo(new SimulationStateVersion(5));
        assertThat(state.clock().currentTime()).isEqualTo(Instant.parse("2026-08-06T10:00:05Z"));

        assertThatThrownBy(() -> reducer.apply(state, event(6, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("restart"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("terminal");
    }

    @Test
    void rejectsOutOfSequenceEventsAndInvalidStepTransitions() {
        var created = reducer.apply(SimulationState.initial(sessionId), created());

        assertThatThrownBy(() -> reducer.apply(created, event(3, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("gap"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("contiguous");

        var running = reducer.apply(created, event(2, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("start")));
        assertThatThrownBy(() -> reducer.apply(running, event(3, LaboratoryEventType.STEP_STARTED, new StepStartedPayload("dependent", List.of("EQ-1")))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("dependencies");

        assertThatThrownBy(() -> reducer.apply(running, event(3, LaboratoryEventType.STEP_COMPLETED, new StepCompletedPayload("prep", true, Map.of("note", "not running")))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("running");
    }

    @Test
    void controlsStepAvailabilityOptionalSkippingAndResourceAllocation() {
        var state = reduce(
                created(),
                event(2, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("start")),
                event(3, LaboratoryEventType.STEP_STARTED, new StepStartedPayload("prep", List.of("EQ-BALANCE-1"))));

        assertThat(state.step("prep").status()).isEqualTo(ProcessStepExecutionStatus.RUNNING);
        assertThat(state.equipmentAllocations()).containsKey("EQ-BALANCE-1");

        var allocatedState = state;
        assertThatThrownBy(() -> reducer.apply(allocatedState, event(4, LaboratoryEventType.STEP_STARTED, new StepStartedPayload("parallel", List.of("EQ-BALANCE-1")))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("allocated");

        state = reducer.apply(state, event(4, LaboratoryEventType.STEP_COMPLETED, new StepCompletedPayload("prep", true, Map.of("mass", "1.0 g"))));
        assertThat(state.equipmentAllocations()).doesNotContainKey("EQ-BALANCE-1");
        assertThat(state.step("dependent").status()).isEqualTo(ProcessStepExecutionStatus.AVAILABLE);

        state = reducer.apply(state, event(5, LaboratoryEventType.STEP_SKIPPED, new StepSkippedPayload("optional-sample", "not needed")));
        assertThat(state.step("optional-sample").status()).isEqualTo(ProcessStepExecutionStatus.SKIPPED);

        var afterOptionalSkip = state;
        assertThatThrownBy(() -> reducer.apply(afterOptionalSkip, event(6, LaboratoryEventType.STEP_SKIPPED, new StepSkippedPayload("dependent", "cannot skip"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("mandatory");

        state = reducer.apply(state, event(6, LaboratoryEventType.STEP_STARTED, new StepStartedPayload("dependent", List.of())));
        state = reducer.apply(state, event(7, LaboratoryEventType.STEP_FAILED, new StepFailedPayload("dependent", "explicit failure")));
        assertThat(state.step("dependent").status()).isEqualTo(ProcessStepExecutionStatus.FAILED);
    }

    @Test
    void conservesMaterialAcrossDispenseTransferSampleAndMixingBookkeeping() {
        var state = reduce(
                created(),
                event(2, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("start")),
                event(3, LaboratoryEventType.MATERIAL_DISPENSED, new MaterialDispensedPayload("vessel-a", "CON-DWK", "COMP-H2O", new BigDecimal("80"), "mL", "AQUEOUS", new BigDecimal("100"))),
                event(4, LaboratoryEventType.MATERIAL_TRANSFERRED, new MaterialTransferredPayload("vessel-a", "vessel-b", "COMP-H2O", new BigDecimal("30"), "mL", "AQUEOUS", new BigDecimal("100"))),
                event(5, LaboratoryEventType.SAMPLE_TAKEN, new SampleTakenPayload("vessel-b", "sample-1", "COMP-H2O", new BigDecimal("10"), "mL", "AQUEOUS")),
                event(6, LaboratoryEventType.MATERIAL_MIXED, new MaterialMixedPayload("vessel-a", "bookkeeping only")));

        assertThat(state.quantity("vessel-a", "COMP-H2O", "mL")).isEqualByComparingTo("50");
        assertThat(state.quantity("vessel-b", "COMP-H2O", "mL")).isEqualByComparingTo("20");
        assertThat(state.quantity("sample-1", "COMP-H2O", "mL")).isEqualByComparingTo("10");
        assertThat(state.totalQuantity("COMP-H2O", "mL")).isEqualByComparingTo("80");
        assertThat(state.vessel("vessel-a").lastMixingNote()).isEqualTo("bookkeeping only");

        assertThatThrownBy(() -> reducer.apply(state, event(7, LaboratoryEventType.MATERIAL_TRANSFERRED,
                new MaterialTransferredPayload("vessel-b", "vessel-c", "COMP-H2O", new BigDecimal("25"), "mL", "AQUEOUS", new BigDecimal("100")))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("exceed");

        assertThatThrownBy(() -> reducer.apply(state, event(7, LaboratoryEventType.MATERIAL_DISPENSED,
                new MaterialDispensedPayload("vessel-b", "CON-DWK", "COMP-H2O", new BigDecimal("200"), "mL", "AQUEOUS", new BigDecimal("100")))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("working volume");
    }

    @Test
    void replayIsDeterministic() {
        var events = List.of(
                created(),
                event(2, LaboratoryEventType.SESSION_STARTED, new SessionLifecyclePayload("start")),
                event(3, LaboratoryEventType.STEP_STARTED, new StepStartedPayload("prep", List.of())),
                event(4, LaboratoryEventType.STEP_COMPLETED, new StepCompletedPayload("prep", true, Map.of())),
                event(5, LaboratoryEventType.MATERIAL_DISPENSED, new MaterialDispensedPayload("vessel-a", "CON-DWK", "COMP-H2O", new BigDecimal("1"), "mL", "AQUEOUS", new BigDecimal("100"))));

        assertThat(reducer.replay(sessionId, events)).isEqualTo(reduce(events.toArray(LaboratoryEvent[]::new)));
    }

    private SimulationState reduce(LaboratoryEvent... events) {
        var state = SimulationState.initial(sessionId);
        for (LaboratoryEvent event : events) {
            state = reducer.apply(state, event);
        }
        return state;
    }

    private LaboratoryEvent created() {
        return event(1, LaboratoryEventType.SESSION_CREATED, new SessionCreatedPayload(
                "PROC-13", 1, List.of(
                new StepDefinitionSnapshot("prep", false, List.of()),
                new StepDefinitionSnapshot("parallel", false, List.of()),
                new StepDefinitionSnapshot("dependent", false, List.of("prep")),
                new StepDefinitionSnapshot("optional-sample", true, List.of("prep")))));
    }

    private LaboratoryEvent event(long sequence, LaboratoryEventType type, LaboratoryEventPayload payload) {
        return new LaboratoryEvent(
                new LaboratoryEventId("EV-" + sequence),
                sessionId,
                new LaboratoryEventSequence(sequence),
                new SimulationStateVersion(sequence),
                Instant.parse("2026-08-06T10:00:0" + sequence + "Z"),
                Instant.parse("2026-08-06T10:00:0" + sequence + "Z"),
                type,
                1,
                new LaboratoryEventSource("test", "operator"),
                new CorrelationId("CORR-1"),
                sequence == 1 ? null : new CausationId("EV-" + (sequence - 1)),
                new IdempotencyKey("IDEMP-" + sequence),
                payload);
    }
}

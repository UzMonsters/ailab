package com.ailab.chemistry;

import com.ailab.chemistry.api.LaboratoryProcessService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepCompletedPayload;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStatus;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStep;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessVersion;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessContainerRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessEquipmentRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessMaterialRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepDependency;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepId;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepType;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionStatus;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class Phase13ProcessEventsAndStateIntegrationTest {
    @Autowired
    private LaboratoryProcessService processService;

    @Autowired
    private SimulationSessionService sessionService;

    @Autowired
    private LaboratoryEventStore eventStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @Test
    void servicesInjectAndLatestMigrationsAreApplied() {
        assertThat(processService).isNotNull();
        assertThat(sessionService).isNotNull();
        assertThat(eventStore.getClass().getName()).contains("Jdbc");
        assertThat(chemistryFlyway.info().current().getVersion().getVersion()).isEqualTo("51");
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.laboratory_process_definitions", Integer.class)).isNotNull();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chemistry.simulation_events", Integer.class)).isNotNull();
    }

    @Test
    void jdbcProcessLookupEventAppendSnapshotAndReplayAreDeterministic() {
        var process = processService.publish(processService.create(process("PROC-" + UUID.randomUUID())));
        assertThat(process.status()).isEqualTo(LaboratoryProcessStatus.PUBLISHED);
        assertThat(processService.get(process.code(), process.version().value())).isEqualTo(process);

        var session = sessionService.createSession(new CreateSimulationSessionRequest(
                new SimulationSessionId("SIM-" + UUID.randomUUID()),
                process.code(),
                process.version().value(),
                Instant.parse("2026-08-06T10:00:00Z")));
        assertThat(session.status()).isEqualTo(SimulationSessionStatus.CREATED);

        var started = sessionService.appendEvent(session.sessionId(), 1, new IdempotencyKey("start"),
                new SessionLifecyclePayload("operator start"));
        assertThat(started.status()).isEqualTo(SimulationSessionStatus.RUNNING);

        var idempotentRetry = sessionService.appendEvent(session.sessionId(), 1, new IdempotencyKey("start"),
                new SessionLifecyclePayload("operator start"));
        assertThat(idempotentRetry).isEqualTo(started);

        assertThatThrownBy(() -> sessionService.appendEvent(session.sessionId(), 2, new IdempotencyKey("start"),
                new MaterialDispensedPayload("vessel-a", "CON-DWK-KIMAX-28014B-100-VOLUMETRIC", "COMP-H2O",
                        new BigDecimal("10"), "mL", "AQUEOUS", new BigDecimal("100"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("idempotency");

        var stepStarted = sessionService.appendEvent(session.sessionId(), 2, new IdempotencyKey("step-start"),
                new StepStartedPayload("prep", List.of("EQ-OHAUS-PX224-MASS")));
        assertThat(stepStarted.equipmentAllocations()).containsKey("EQ-OHAUS-PX224-MASS");

        var stepDone = sessionService.appendEvent(session.sessionId(), 3, new IdempotencyKey("step-done"),
                new StepCompletedPayload("prep", true, Map.of("explicitOutcome", "weighed sample")));
        assertThat(stepDone.equipmentAllocations()).doesNotContainKey("EQ-OHAUS-PX224-MASS");

        var dispensed = sessionService.appendEvent(session.sessionId(), 4, new IdempotencyKey("dispense-water"),
                new MaterialDispensedPayload("vessel-a", "CON-DWK-KIMAX-28014B-100-VOLUMETRIC", "COMP-H2O",
                        new BigDecimal("80"), "mL", "AQUEOUS", new BigDecimal("100")));
        assertThat(dispensed.quantity("vessel-a", "COMP-H2O", "mL")).isEqualByComparingTo("80");

        assertThat(sessionService.getCurrentState(session.sessionId())).isEqualTo(dispensed);
        assertThat(sessionService.replay(session.sessionId())).isEqualTo(dispensed);
        assertThat(sessionService.replayFromLatestSnapshot(session.sessionId())).isEqualTo(dispensed);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.simulation_snapshots WHERE session_id = ?",
                Integer.class,
                session.sessionId().value())).isGreaterThanOrEqualTo(1);
        assertThat(eventStore.eventsForSession(session.sessionId()).stream().map(event -> event.sequence().value()).toList())
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void staleVersionIncompatibleContainerAndConcurrentAppendsAreRejectedAtomically() throws Exception {
        var process = processService.publish(processService.create(process("PROC-" + UUID.randomUUID())));
        var session = sessionService.createSession(new CreateSimulationSessionRequest(
                new SimulationSessionId("SIM-" + UUID.randomUUID()),
                process.code(),
                process.version().value(),
                Instant.parse("2026-08-06T10:00:00Z")));
        var running = sessionService.appendEvent(session.sessionId(), 1, new IdempotencyKey("start"), new SessionLifecyclePayload("start"));

        assertThatThrownBy(() -> sessionService.appendEvent(session.sessionId(), 1, new IdempotencyKey("stale"),
                new MaterialDispensedPayload("vessel-a", "CON-DWK-KIMAX-28014B-100-VOLUMETRIC", "COMP-H2O",
                        BigDecimal.ONE, "mL", "AQUEOUS", new BigDecimal("100"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("stale");

        int beforeRejected = eventStore.eventsForSession(session.sessionId()).size();
        assertThatThrownBy(() -> sessionService.appendEvent(session.sessionId(), running.version().value(), new IdempotencyKey("bad-container"),
                new MaterialDispensedPayload("vessel-a", "CON-HDPE-NARROW-MOUTH-500", "FAMILY-AROMATIC-HYDROCARBONS",
                        BigDecimal.ONE, "mL", "LIQUID", new BigDecimal("450"))))
                .isInstanceOf(SimulationStateException.class)
                .hasMessageContaining("container");
        assertThat(eventStore.eventsForSession(session.sessionId())).hasSize(beforeRejected);

        var tasks = new ArrayList<Callable<Boolean>>();
        tasks.add(() -> tryAppend(session.sessionId(), running.version().value(), "concurrent-a", "vessel-a"));
        tasks.add(() -> tryAppend(session.sessionId(), running.version().value(), "concurrent-b", "vessel-b"));
        try (var executor = Executors.newFixedThreadPool(2)) {
            var outcomes = executor.invokeAll(tasks).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .toList();
            assertThat(outcomes).containsExactlyInAnyOrder(true, false);
        }
    }

    private boolean tryAppend(SimulationSessionId sessionId, long expectedVersion, String key, String vessel) {
        try {
            sessionService.appendEvent(sessionId, expectedVersion, new IdempotencyKey(key),
                    new MaterialDispensedPayload(vessel, "CON-DWK-KIMAX-28014B-100-VOLUMETRIC", "COMP-H2O",
                            BigDecimal.ONE, "mL", "AQUEOUS", new BigDecimal("100")));
            return true;
        } catch (SimulationStateException ex) {
            return false;
        }
    }

    private LaboratoryProcessDefinition process(String code) {
        return new LaboratoryProcessDefinition(code, new LaboratoryProcessVersion(1), LaboratoryProcessStatus.DRAFT, List.of(
                step("prep", ProcessStepType.MEASURE, false, List.of()),
                step("dispense", ProcessStepType.DISPENSE, false, List.of("prep"))));
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

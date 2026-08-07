package com.ailab.chemistry;

import com.ailab.chemistry.api.LaboratoryProcessService;
import com.ailab.chemistry.api.SimulationEngineService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventStore;
import com.ailab.chemistry.domain.laboratoryevent.LaboratoryEventType;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
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
import com.ailab.chemistry.domain.simulationengine.ConservationDimension;
import com.ailab.chemistry.domain.simulationengine.ConservationStatus;
import com.ailab.chemistry.domain.simulationengine.MaterialStateDelta;
import com.ailab.chemistry.domain.simulationengine.ScientificDatasetReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelSelection;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.ailab.chemistry.domain.simulationengine.SimulationCalculationAuditRepository;
import com.ailab.chemistry.domain.simulationengine.SimulationCommand;
import com.ailab.chemistry.domain.simulationengine.SimulationCommandId;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionErrorCode;
import com.ailab.chemistry.domain.simulationengine.SimulationExecutionException;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationStateException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"test", "local"})
class Phase14SimulationEngineIntegrationTest {
    @Autowired
    private LaboratoryProcessService processService;

    @Autowired
    private SimulationSessionService sessionService;

    @Autowired
    private SimulationEngineService simulationEngineService;

    @Autowired
    private SimulationCalculationAuditRepository auditRepository;

    @Autowired
    private LaboratoryEventStore eventStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("chemistryFlyway")
    private Flyway chemistryFlyway;

    @Test
    void servicesInjectAndLatestSimulationEngineMigrationsAreApplied() {
        assertThat(simulationEngineService).isNotNull();
        assertThat(auditRepository.getClass().getName()).contains("Jdbc");
        assertThat(chemistryFlyway.info().current().getVersion().getVersion()).isEqualTo("51");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.simulation_calculation_audits", Integer.class)).isNotNull();
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.simulation_scientific_event_schemas WHERE event_type = 'STOICHIOMETRIC_REACTION_APPLIED'",
                Integer.class)).isEqualTo(1);
    }

    @Test
    void explicitStoichiometricOperationCreatesOneAuthoritativeEventAuditAndReplayDelta() {
        RunningSession fixture = runningSession(ProcessStepType.MIX, SimulationOperationType.STOICHIOMETRIC_REACTION);
        dispense(fixture.sessionId(), "h2", "COMP-H2", "2.0", "GAS");
        dispense(fixture.sessionId(), "o2", "COMP-O2", "1.0", "GAS");
        long beforeVersion = sessionService.getCurrentState(fixture.sessionId()).version().value();

        var result = simulationEngineService.execute(fixture.sessionId(), beforeVersion, new IdempotencyKey("water-synthesis"),
                command("cmd-water", "react", "vessel-a", SimulationOperationType.STOICHIOMETRIC_REACTION,
                        "stoichiometry", "RXN-WATER-SYNTHESIS",
                        Map.of("extentMol", "1.0", "extentMode", "EXPLICIT_REACTION_EXTENT"),
                        List.of(
                                remove("COMP-H2", "2.0", "GAS"),
                                remove("COMP-O2", "1.0", "GAS"),
                                add("COMP-H2O", "2.0", "GAS"))));

        assertThat(result.state().quantity("vessel-a", "COMP-H2", "mol")).isZero();
        assertThat(result.state().quantity("vessel-a", "COMP-O2", "mol")).isZero();
        assertThat(result.state().quantity("vessel-a", "COMP-H2O", "mol")).isEqualByComparingTo("2.0");
        assertThat(result.payload().eventType()).isEqualTo(LaboratoryEventType.STOICHIOMETRIC_REACTION_APPLIED);
        assertThat(result.payload().conservationLedger().residual(ConservationDimension.ELEMENT_ATOMS).status())
                .isEqualTo(ConservationStatus.SATISFIED);

        var events = eventStore.eventsForSession(fixture.sessionId());
        assertThat(events.stream().filter(event -> event.type() == LaboratoryEventType.STOICHIOMETRIC_REACTION_APPLIED)).hasSize(1);
        assertThat(auditRepository.find(fixture.sessionId(), result.eventId())).contains(result.audit());
        assertThat(sessionService.replay(fixture.sessionId())).isEqualTo(result.state());
        assertThat(sessionService.replayFromLatestSnapshot(fixture.sessionId())).isEqualTo(result.state());

        var retry = simulationEngineService.execute(fixture.sessionId(), beforeVersion, new IdempotencyKey("water-synthesis"),
                command("cmd-water", "react", "vessel-a", SimulationOperationType.STOICHIOMETRIC_REACTION,
                        "stoichiometry", "RXN-WATER-SYNTHESIS",
                        Map.of("extentMol", "1.0", "extentMode", "EXPLICIT_REACTION_EXTENT"),
                        List.of(remove("COMP-H2", "2.0", "GAS"), remove("COMP-O2", "1.0", "GAS"), add("COMP-H2O", "2.0", "GAS"))));
        assertThat(retry).isEqualTo(result);

        assertThatThrownBy(() -> simulationEngineService.execute(fixture.sessionId(), beforeVersion,
                new IdempotencyKey("water-synthesis"),
                command("cmd-conflict", "react", "vessel-a", SimulationOperationType.BOOKKEEPING_MIX,
                        "bookkeeping", "MIX-NO-CHEMISTRY", Map.of("mixingNote", "changed"), List.of())))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.IDEMPOTENCY_CONFLICT);
    }

    @Test
    void explicitSelectionPreventsAutomaticReactionKineticsAndPhaseChanges() {
        RunningSession mix = runningSession(ProcessStepType.MIX, SimulationOperationType.BOOKKEEPING_MIX);
        dispense(mix.sessionId(), "mix-h2", "COMP-H2", "2.0", "GAS");
        dispense(mix.sessionId(), "mix-o2", "COMP-O2", "1.0", "GAS");
        long mixVersion = sessionService.getCurrentState(mix.sessionId()).version().value();

        var mixed = simulationEngineService.execute(mix.sessionId(), mixVersion, new IdempotencyKey("bookkeeping-mix"),
                command("cmd-bookkeeping", "react", "vessel-a", SimulationOperationType.BOOKKEEPING_MIX,
                        "bookkeeping", "MIX-NO-CHEMISTRY", Map.of("mixingNote", "combined without chemistry"), List.of()));
        assertThat(mixed.state().quantity("vessel-a", "COMP-H2O", "mol")).isZero();

        RunningSession heat = runningSession(ProcessStepType.HEAT, SimulationOperationType.THERMAL_OPERATION);
        dispense(heat.sessionId(), "heat-water", "COMP-H2O", "10.0", "LIQUID");
        long heatVersion = sessionService.getCurrentState(heat.sessionId()).version().value();
        var heated = simulationEngineService.execute(heat.sessionId(), heatVersion, new IdempotencyKey("sensible-heat"),
                command("cmd-heat", "react", "vessel-a", SimulationOperationType.THERMAL_OPERATION,
                        "calorimetry", "SENSIBLE_HEATING",
                        Map.of("targetTemperatureK", "350.0", "heatInputJ", "2194.5"),
                        List.of()));
        assertThat(heated.state().quantity("vessel-a", "COMP-H2O", "mol")).isEqualByComparingTo("10.0");
        assertThat(heated.payload().stateDelta().vesselDeltas().getFirst().materialDeltas()).isEmpty();

        RunningSession hold = runningSession(ProcessStepType.HOLD, SimulationOperationType.BOOKKEEPING_MIX);
        assertThatThrownBy(() -> simulationEngineService.execute(hold.sessionId(), hold.currentVersion(),
                new IdempotencyKey("auto-kinetics"),
                command("cmd-auto-kinetics", "react", "vessel-a", SimulationOperationType.KINETIC_PROGRESS,
                        "kinetics", "PROFILE-ACTIVE", Map.of("durationSeconds", "10"), List.of())))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.OPERATION_NOT_ALLOWED_FOR_STEP);
    }

    @Test
    void supportedOperationHandlersPersistTraceableStateDeltasAndRejectUnsupportedModelsAtomically() {
        RunningSession equilibrium = runningSession(ProcessStepType.MIX, SimulationOperationType.EQUILIBRIUM_REACTION);
        dispense(equilibrium.sessionId(), "co", "COMP-CO", "1.0", "GAS");
        dispense(equilibrium.sessionId(), "oxygen", "COMP-O2", "0.5", "GAS");
        var equilibriumResult = simulationEngineService.execute(equilibrium.sessionId(),
                sessionService.getCurrentState(equilibrium.sessionId()).version().value(),
                new IdempotencyKey("co-equilibrium"),
                command("cmd-equilibrium", "react", "vessel-a", SimulationOperationType.EQUILIBRIUM_REACTION,
                        "equilibrium-composition", "RXN-CO-OXIDATION",
                        Map.of("temperatureK", "1200", "model", "IDEAL_GAS_LOG_SOLVER"),
                        List.of(remove("COMP-CO", "0.25", "GAS"), remove("COMP-O2", "0.125", "GAS"), add("COMP-CO2", "0.25", "GAS"))));
        assertThat(equilibriumResult.state().quantity("vessel-a", "COMP-CO2", "mol")).isEqualByComparingTo("0.25");

        RunningSession kinetic = runningSession(ProcessStepType.HOLD, SimulationOperationType.KINETIC_PROGRESS);
        dispense(kinetic.sessionId(), "kinetic-a", "COMP-RAD-H", "1.0", "mol", "GAS");
        var kineticResult = simulationEngineService.execute(kinetic.sessionId(),
                sessionService.getCurrentState(kinetic.sessionId()).version().value(),
                new IdempotencyKey("kinetic-progress"),
                command("cmd-kinetic", "react", "vessel-a", SimulationOperationType.KINETIC_PROGRESS,
                        "kinetics", "PROFILE-ELEM-H-O2",
                        Map.of("durationSeconds", "2.0", "temperatureK", "900.0", "solverStatus", "SUCCESS"),
                        List.of(remove("COMP-RAD-H", "0.2", "GAS"), add("COMP-HO2", "0.2", "GAS"))));
        assertThat(kineticResult.payload().calculationTrace().intermediatePoints()).contains("t=1.0s");

        RunningSession gas = runningSession(ProcessStepType.HOLD, SimulationOperationType.GAS_STATE_CHANGE);
        var gasResult = simulationEngineService.execute(gas.sessionId(), gas.currentVersion(), new IdempotencyKey("gas-constant-volume"),
                command("cmd-gas", "react", "vessel-a", SimulationOperationType.GAS_STATE_CHANGE,
                        "gas-law", "CONSTANT_VOLUME",
                        Map.of("initialPressureKPa", "101.325", "finalPressureKPa", "121.59", "compressibilityFactor", "1.000"),
                        List.of()));
        assertThat(gasResult.payload().calculationTrace().result().values()).containsEntry("finalPressureKPa", "121.59");

        RunningSession phase = runningSession(ProcessStepType.HEAT, SimulationOperationType.PHASE_TRANSITION);
        dispense(phase.sessionId(), "ice", "COMP-H2O", "1.0", "mol", "SOLID");
        var phaseResult = simulationEngineService.execute(phase.sessionId(),
                sessionService.getCurrentState(phase.sessionId()).version().value(),
                new IdempotencyKey("water-fusion"),
                command("cmd-phase", "react", "vessel-a", SimulationOperationType.PHASE_TRANSITION,
                        "phase-behavior", "COMP-H2O-FUSION",
                        Map.of("transition", "FUSION", "latentHeatJ", "6010"),
                        List.of(remove("COMP-H2O", "1.0", "SOLID"), add("COMP-H2O", "1.0", "LIQUID"))));
        assertThat(phaseResult.state().quantity("vessel-a", "COMP-H2O", "mol", "LIQUID")).isEqualByComparingTo("1.0");

        RunningSession electrolysis = runningSession(ProcessStepType.HOLD, SimulationOperationType.ELECTROLYSIS);
        dispense(electrolysis.sessionId(), "cu2", "COMP-CU2PLUS", "0.000207", "mol", "AQUEOUS");
        var electrolysisResult = simulationEngineService.execute(electrolysis.sessionId(),
                sessionService.getCurrentState(electrolysis.sessionId()).version().value(),
                new IdempotencyKey("copper-deposition"),
                command("cmd-electrolysis", "react", "vessel-a", SimulationOperationType.ELECTROLYSIS,
                        "electrochemistry", "CU2PLUS_CU_CATHODE",
                        Map.of("currentA", "2", "durationSeconds", "20", "efficiency", "1.0", "faradayMoles", "0.000207"),
                        List.of(remove("COMP-CU2PLUS", "0.000207", "AQUEOUS"), add("COMP-CU", "0.000207", "SOLID"))));
        assertThat(electrolysisResult.state().quantity("vessel-a", "COMP-CU", "mol")).isEqualByComparingTo("0.000207");
        assertThat(electrolysisResult.state().totalQuantity("ELECTRON", "mol")).isZero();

        int beforeEvents = eventStore.eventsForSession(equilibrium.sessionId()).size();
        assertThatThrownBy(() -> simulationEngineService.execute(equilibrium.sessionId(), equilibriumResult.state().version().value(),
                new IdempotencyKey("unsupported-model"),
                command("cmd-unsupported", "react", "vessel-a", SimulationOperationType.EQUILIBRIUM_REACTION,
                        "unsupported-equilibrium", "RXN-CO-OXIDATION", Map.of("temperatureK", "1200"), List.of())))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.UNSUPPORTED_MODEL_SELECTION);
        assertThat(eventStore.eventsForSession(equilibrium.sessionId())).hasSize(beforeEvents);
    }

    @Test
    void validationFailureSuitabilityFailureAndSameVersionConcurrencyAreAtomic() throws Exception {
        RunningSession invalid = runningSession(ProcessStepType.MIX, SimulationOperationType.STOICHIOMETRIC_REACTION);
        dispense(invalid.sessionId(), "h2-inv", "COMP-H2", "2.0", "GAS");
        dispense(invalid.sessionId(), "o2-inv", "COMP-O2", "1.0", "GAS");
        long currentVersion = sessionService.getCurrentState(invalid.sessionId()).version().value();
        int beforeInvalidEvents = eventStore.eventsForSession(invalid.sessionId()).size();
        assertThatThrownBy(() -> simulationEngineService.execute(invalid.sessionId(), currentVersion,
                new IdempotencyKey("excessive-extent"),
                command("cmd-excessive", "react", "vessel-a", SimulationOperationType.STOICHIOMETRIC_REACTION,
                        "stoichiometry", "RXN-WATER-SYNTHESIS", Map.of("extentMol", "10.0"),
                        List.of(remove("COMP-H2", "20.0", "GAS"), add("COMP-H2O", "20.0", "GAS")))))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.STATE_DELTA_INVARIANT_FAILED);
        assertThat(eventStore.eventsForSession(invalid.sessionId())).hasSize(beforeInvalidEvents);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.simulation_calculation_audits WHERE session_id = ?",
                Integer.class, invalid.sessionId().value())).isZero();

        RunningSession heat = runningSession(ProcessStepType.HEAT, SimulationOperationType.THERMAL_OPERATION);
        assertThatThrownBy(() -> simulationEngineService.execute(heat.sessionId(), heat.currentVersion(),
                new IdempotencyKey("too-hot"),
                command("cmd-too-hot", "react", "vessel-a", SimulationOperationType.THERMAL_OPERATION,
                        "calorimetry", "SENSIBLE_HEATING", Map.of("targetTemperatureK", "5000"), List.of())))
                .isInstanceOf(SimulationExecutionException.class)
                .extracting("errorCode")
                .isEqualTo(SimulationExecutionErrorCode.SUITABILITY_REJECTED);

        RunningSession concurrent = runningSession(ProcessStepType.MIX, SimulationOperationType.STOICHIOMETRIC_REACTION);
        dispense(concurrent.sessionId(), "conc-h2", "COMP-H2", "4.0", "GAS");
        dispense(concurrent.sessionId(), "conc-o2", "COMP-O2", "2.0", "GAS");
        long version = sessionService.getCurrentState(concurrent.sessionId()).version().value();
        List<Callable<Boolean>> tasks = List.of(
                () -> tryExecute(concurrent.sessionId(), version, "parallel-a", "cmd-parallel-a"),
                () -> tryExecute(concurrent.sessionId(), version, "parallel-b", "cmd-parallel-b"));
        try (var executor = Executors.newFixedThreadPool(2)) {
            var outcomes = executor.invokeAll(tasks).stream().map(future -> {
                try {
                    return future.get();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).toList();
            assertThat(outcomes).containsExactlyInAnyOrder(true, false);
        }
        assertThat(eventStore.eventsForSession(concurrent.sessionId()).stream()
                .filter(event -> event.type() == LaboratoryEventType.STOICHIOMETRIC_REACTION_APPLIED)).hasSize(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chemistry.simulation_calculation_audits WHERE session_id = ?",
                Integer.class, concurrent.sessionId().value())).isEqualTo(1);
    }

    private boolean tryExecute(SimulationSessionId sessionId, long version, String key, String commandId) {
        try {
            simulationEngineService.execute(sessionId, version, new IdempotencyKey(key),
                    command(commandId, "react", "vessel-a", SimulationOperationType.STOICHIOMETRIC_REACTION,
                            "stoichiometry", "RXN-WATER-SYNTHESIS", Map.of("extentMol", "1.0"),
                            List.of(remove("COMP-H2", "2.0", "GAS"), remove("COMP-O2", "1.0", "GAS"), add("COMP-H2O", "2.0", "GAS"))));
            return true;
        } catch (SimulationExecutionException | SimulationStateException ex) {
            return false;
        }
    }

    private RunningSession runningSession(ProcessStepType type, SimulationOperationType operationType) {
        var process = processService.publish(processService.create(process("PROC14-" + UUID.randomUUID(), type, operationType)));
        var session = sessionService.createSession(new CreateSimulationSessionRequest(
                new SimulationSessionId("SIM14-" + UUID.randomUUID()),
                process.code(),
                process.version().value(),
                Instant.parse("2026-08-07T10:00:00Z")));
        var running = sessionService.appendEvent(session.sessionId(), 1, new IdempotencyKey("start"), new SessionLifecyclePayload("start"));
        var stepStarted = sessionService.appendEvent(session.sessionId(), running.version().value(), new IdempotencyKey("start-react"),
                new StepStartedPayload("react", List.of()));
        return new RunningSession(session.sessionId(), stepStarted.version().value());
    }

    private LaboratoryProcessDefinition process(String code, ProcessStepType type, SimulationOperationType operationType) {
        return new LaboratoryProcessDefinition(code, new LaboratoryProcessVersion(1), LaboratoryProcessStatus.DRAFT, List.of(
                step("react", type, operationType)));
    }

    private LaboratoryProcessStep step(String id, ProcessStepType type, SimulationOperationType operationType) {
        return new LaboratoryProcessStep(
                new ProcessStepId(id),
                type,
                false,
                Duration.of("1", DurationUnit.SECOND),
                List.<ProcessStepDependency>of(),
                List.of(new ProcessMaterialRequirement("mat-" + id, "COMP-H2O", BigDecimal.ONE, "mol", "GAS", true, true)),
                List.of(new ProcessEquipmentRequirement("eq-" + id, "EQ-OHAUS-PX224-MASS", false)),
                List.of(new ProcessContainerRequirement("con-" + id, "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                        BigDecimal.ONE, false, "COMP-H2O", "GAS")),
                List.of(),
                List.of("input-" + id),
                List.of("output-" + id))
                .withScientificOperationSpecifications(List.of(
                        new ScientificOperationSpecification(operationType, modelFor(operationType))));
    }

    private void dispense(SimulationSessionId sessionId, String key, String compound, String amount, String state) {
        dispense(sessionId, key, compound, amount, "mol", state);
    }

    private void dispense(SimulationSessionId sessionId, String key, String compound, String amount, String unit, String state) {
        long version = sessionService.getCurrentState(sessionId).version().value();
        sessionService.appendEvent(sessionId, version, new IdempotencyKey(key),
                new MaterialDispensedPayload("vessel-a", "", compound, new BigDecimal(amount), unit, state, BigDecimal.ZERO));
    }

    private SimulationCommand command(String id, String stepId, String vesselId, SimulationOperationType operationType,
                                      String method, String reactionOrProfile, Map<String, String> inputs,
                                      List<MaterialStateDelta> materialDeltas) {
        Map<String, String> mergedInputs = new HashMap<>(inputs);
        mergedInputs.putIfAbsent("fumeHoodOperating", "true");
        return new SimulationCommand(
                new SimulationCommandId(id),
                stepId,
                vesselId,
                new ScientificOperationSpecification(operationType, model(method, reactionOrProfile)),
                mergedInputs,
                materialDeltas);
    }

    private ScientificModelSelection modelFor(SimulationOperationType operationType) {
        return switch (operationType) {
            case STOICHIOMETRIC_REACTION -> model("stoichiometry", "RXN-WATER-SYNTHESIS");
            case EQUILIBRIUM_REACTION -> model("equilibrium-composition", "RXN-CO-OXIDATION");
            case KINETIC_PROGRESS -> model("kinetics", "PROFILE-ELEM-H-O2");
            case THERMAL_OPERATION -> model("calorimetry", "SENSIBLE_HEATING");
            case GAS_STATE_CHANGE -> model("gas-law", "CONSTANT_VOLUME");
            case PHASE_TRANSITION -> model("phase-behavior", "COMP-H2O-FUSION");
            case ELECTROLYSIS -> model("electrochemistry", "CU2PLUS_CU_CATHODE");
            case BOOKKEEPING_MIX -> model("bookkeeping", "MIX-NO-CHEMISTRY");
        };
    }

    private ScientificModelSelection model(String method, String reactionOrProfile) {
        return new ScientificModelSelection(
                method,
                reactionOrProfile,
                new ScientificModelReference(method + "-model", "1.0.0"),
                List.of(new ScientificDatasetReference(method + "-dataset", "1.0.0")),
                Map.of("selection", "explicit"));
    }

    private MaterialStateDelta add(String compoundCode, String amount, String physicalState) {
        return new MaterialStateDelta("vessel-a", compoundCode, new BigDecimal(amount), "mol", physicalState);
    }

    private MaterialStateDelta remove(String compoundCode, String amount, String physicalState) {
        return new MaterialStateDelta("vessel-a", compoundCode, new BigDecimal(amount).negate(), "mol", physicalState);
    }

    private record RunningSession(SimulationSessionId sessionId, long currentVersion) {
    }
}

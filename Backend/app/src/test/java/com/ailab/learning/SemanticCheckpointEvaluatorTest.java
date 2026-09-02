package com.ailab.learning;

import com.ailab.learning.dto.LearningDtos.CheckpointDefinitionDto;
import com.ailab.learning.dto.LearningDtos.EvaluateCheckpointResponse;
import com.ailab.learning.evaluator.SemanticCheckpointEvaluator;
import com.ailab.workspace.domain.MeasurementEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.MeasurementRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemanticCheckpointEvaluatorTest {

    @Mock
    private WorkspaceStateRepository stateRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SemanticCheckpointEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new SemanticCheckpointEvaluator(stateRepository, measurementRepository, objectMapper);
    }

    @Test
    void testEvaluateContainerPresent_Success() {
        WorkspaceStateEntity state = new WorkspaceStateEntity("ws-1", 1);
        state.setItemsJson("[{\"id\":\"beaker-1\",\"catalogCode\":\"beaker_250ml\",\"capabilities\":{\"container\":{\"capacity\":250}}}]");
        when(stateRepository.findById("ws-1")).thenReturn(Optional.of(state));

        CheckpointDefinitionDto checkpoint = new CheckpointDefinitionDto("CONTAINER_PRESENT", null, Map.of("equipmentCode", "beaker"), null);

        EvaluateCheckpointResponse response = evaluator.evaluate("ws-1", checkpoint, "next-step");

        assertThat(response.accepted()).isTrue();
        assertThat(response.nextStep()).isEqualTo("next-step");
    }

    @Test
    void testEvaluateContainerPresent_FailsWhenMissing() {
        WorkspaceStateEntity state = new WorkspaceStateEntity("ws-1", 1);
        state.setItemsJson("[]");
        when(stateRepository.findById("ws-1")).thenReturn(Optional.of(state));

        CheckpointDefinitionDto checkpoint = new CheckpointDefinitionDto("CONTAINER_PRESENT", null, Map.of("equipmentCode", "beaker"), null);

        EvaluateCheckpointResponse response = evaluator.evaluate("ws-1", checkpoint, "next-step");

        assertThat(response.accepted()).isFalse();
    }

    @Test
    void testEvaluateMaterialAdded_Success() {
        WorkspaceStateEntity state = new WorkspaceStateEntity("ws-1", 1);
        state.setItemsJson("[{\"id\":\"beaker-1\",\"contents\":[{\"formula\":\"H2O\",\"volumeMl\":60.0}]}]");
        when(stateRepository.findById("ws-1")).thenReturn(Optional.of(state));

        CheckpointDefinitionDto checkpoint = new CheckpointDefinitionDto(
                "MATERIAL_ADDED",
                null,
                Map.of("materialCode", "H2O"),
                Map.of("minVolumeMl", 50.0)
        );

        EvaluateCheckpointResponse response = evaluator.evaluate("ws-1", checkpoint, "step-3");

        assertThat(response.accepted()).isTrue();
    }

    @Test
    void testEvaluateSensorConnected_Success() {
        WorkspaceStateEntity state = new WorkspaceStateEntity("ws-1", 1);
        state.setItemsJson("[{\"id\":\"thermometer-1\",\"catalogCode\":\"thermometer\"},{\"id\":\"beaker-1\",\"catalogCode\":\"beaker\",\"capabilities\":{\"container\":{}}}]");
        state.setConnectionsJson("[{\"sourceItemId\":\"thermometer-1\",\"sourcePortId\":\"sensor-out\",\"targetItemId\":\"beaker-1\",\"targetPortId\":\"sensor-in\"}]");
        when(stateRepository.findById("ws-1")).thenReturn(Optional.of(state));

        CheckpointDefinitionDto checkpoint = new CheckpointDefinitionDto(
                "SENSOR_CONNECTED",
                Map.of("equipmentCode", "thermometer", "portType", "sensor"),
                Map.of("capability", "CONTAINER", "portType", "sensor"),
                null
        );

        EvaluateCheckpointResponse response = evaluator.evaluate("ws-1", checkpoint, "step-done");

        assertThat(response.accepted()).isTrue();
    }

    @Test
    void testEvaluateMeasurement_Success() {
        WorkspaceStateEntity state = new WorkspaceStateEntity("ws-1", 1);
        state.setItemsJson("[]");
        when(stateRepository.findById("ws-1")).thenReturn(Optional.of(state));

        MeasurementEntity measurement = new MeasurementEntity(
                "m-1", "sess-1", "ws-1", "therm-1", "beaker-1", "TEMPERATURE",
                BigDecimal.valueOf(65.5), "CELSIUS", Instant.now()
        );
        when(measurementRepository.findByWorkspaceIdOrderByRecordedAtAsc(eq("ws-1"), any())).thenReturn(List.of(measurement));

        CheckpointDefinitionDto checkpoint = new CheckpointDefinitionDto(
                "MEASUREMENT_RECORDED",
                null,
                null,
                Map.of("sensorType", "TEMPERATURE", "minValue", 50.0)
        );

        EvaluateCheckpointResponse response = evaluator.evaluate("ws-1", checkpoint, null);

        assertThat(response.accepted()).isTrue();
    }
}

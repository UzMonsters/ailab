package com.ailab.learning.evaluator;

import com.ailab.learning.dto.LearningDtos.CheckpointDefinitionDto;
import com.ailab.learning.dto.LearningDtos.EvaluateCheckpointResponse;
import com.ailab.workspace.domain.MeasurementEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.MeasurementRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SemanticCheckpointEvaluator {

    private final WorkspaceStateRepository workspaceStateRepository;
    private final MeasurementRepository measurementRepository;
    private final ObjectMapper objectMapper;

    public SemanticCheckpointEvaluator(
            WorkspaceStateRepository workspaceStateRepository,
            MeasurementRepository measurementRepository,
            ObjectMapper objectMapper
    ) {
        this.workspaceStateRepository = workspaceStateRepository;
        this.measurementRepository = measurementRepository;
        this.objectMapper = objectMapper;
    }

    public EvaluateCheckpointResponse evaluate(String workspaceId, CheckpointDefinitionDto checkpoint, String nextStepId) {
        if (checkpoint == null || checkpoint.factType() == null) {
            return new EvaluateCheckpointResponse(true, null, nextStepId);
        }

        Optional<WorkspaceStateEntity> stateOpt = workspaceStateRepository.findById(workspaceId);
        if (stateOpt.isEmpty()) {
            return new EvaluateCheckpointResponse(false, "Workspace state not found", null);
        }

        WorkspaceStateEntity state = stateOpt.get();
        List<Map<String, Object>> items = parseJsonList(state.getItemsJson());
        List<Map<String, Object>> connections = parseJsonList(state.getConnectionsJson());

        String factType = checkpoint.factType().trim().toUpperCase();

        return switch (factType) {
            case "SENSOR_CONNECTED", "PORT_CONNECTED", "PORT_CONNECTION" ->
                    evaluatePortConnection(checkpoint, items, connections, nextStepId);
            case "CONTAINER_PRESENT", "ITEM_ADDED", "EQUIPMENT_ADDED" ->
                    evaluateItemAdded(checkpoint, items, nextStepId);
            case "MATERIAL_ADDED", "VOLUME_RANGE", "REAGENT_ADDED" ->
                    evaluateMaterialAdded(checkpoint, items, nextStepId);
            case "MEASUREMENT_RECORDED", "VALUE_RANGE", "TEMPERATURE_RANGE", "PH_RANGE" ->
                    evaluateMeasurement(checkpoint, workspaceId, items, nextStepId);
            case "REACTION_COMPLETED", "STATE_REACHED" ->
                    evaluateReactionCompleted(checkpoint, items, nextStepId);
            case "SAFETY_CLEAR" ->
                    new EvaluateCheckpointResponse(true, null, nextStepId);
            default ->
                    evaluateGenericFact(checkpoint, items, connections, nextStepId);
        };
    }

    private EvaluateCheckpointResponse evaluatePortConnection(
            CheckpointDefinitionDto checkpoint,
            List<Map<String, Object>> items,
            List<Map<String, Object>> connections,
            String nextStepId
    ) {
        Map<String, Object> source = checkpoint.source() != null ? checkpoint.source() : Map.of();
        Map<String, Object> target = checkpoint.target() != null ? checkpoint.target() : Map.of();

        String expectedSourceCode = extractString(source, "equipmentCode", "catalogCode", "code", "type");
        String expectedSourcePortType = extractString(source, "portType", "portId", "type");

        String expectedTargetCode = extractString(target, "equipmentCode", "catalogCode", "code", "type");
        String expectedTargetPortType = extractString(target, "portType", "portId", "type");
        String expectedTargetCapability = extractString(target, "capability");

        Map<String, Map<String, Object>> itemsById = new HashMap<>();
        for (Map<String, Object> item : items) {
            String id = extractString(item, "id", "itemId");
            if (id != null) {
                itemsById.put(id, item);
            }
        }

        for (Map<String, Object> conn : connections) {
            String srcId = extractString(conn, "sourceItemId", "fromItemId", "sourceId", "source");
            String tgtId = extractString(conn, "targetItemId", "toItemId", "targetId", "target");
            String srcPort = extractString(conn, "sourcePortId", "fromPortId", "sourcePort", "sourceType");
            String tgtPort = extractString(conn, "targetPortId", "toPortId", "targetPort", "targetType");

            Map<String, Object> srcItem = itemsById.get(srcId);
            Map<String, Object> tgtItem = itemsById.get(tgtId);

            boolean matchDirect = matchesPortPair(srcItem, srcPort, expectedSourceCode, expectedSourcePortType, tgtItem, tgtPort, expectedTargetCode, expectedTargetPortType, expectedTargetCapability);
            boolean matchReversed = matchesPortPair(tgtItem, tgtPort, expectedSourceCode, expectedSourcePortType, srcItem, srcPort, expectedTargetCode, expectedTargetPortType, expectedTargetCapability);

            if (matchDirect || matchReversed) {
                return new EvaluateCheckpointResponse(true, null, nextStepId);
            }
        }

        if (connections.isEmpty()) {
            return new EvaluateCheckpointResponse(false, "No ports are connected yet", null);
        }

        return new EvaluateCheckpointResponse(false, "Required port connection has not been established", null);
    }

    private boolean matchesPortPair(
            Map<String, Object> srcItem, String srcPort, String expSrcCode, String expSrcPort,
            Map<String, Object> tgtItem, String tgtPort, String expTgtCode, String expTgtPort, String expTgtCap
    ) {
        if (!itemMatches(srcItem, expSrcCode)) return false;
        if (!portMatches(srcPort, expSrcPort)) return false;
        if (expTgtCap != null && !itemHasCapability(tgtItem, expTgtCap)) return false;
        if (expTgtCode != null && !itemMatches(tgtItem, expTgtCode)) return false;
        if (!portMatches(tgtPort, expTgtPort)) return false;
        return true;
    }

    private boolean itemMatches(Map<String, Object> item, String expectedCode) {
        if (expectedCode == null || expectedCode.isBlank()) return true;
        if (item == null) return false;
        String code = extractString(item, "catalogCode", "code", "equipmentCode", "type", "id");
        return code != null && code.toLowerCase().contains(expectedCode.toLowerCase());
    }

    private boolean portMatches(String actualPort, String expectedPort) {
        if (expectedPort == null || expectedPort.isBlank()) return true;
        if (actualPort == null) return false;
        return actualPort.toLowerCase().contains(expectedPort.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    private boolean itemHasCapability(Map<String, Object> item, String capability) {
        if (capability == null || capability.isBlank()) return true;
        if (item == null) return false;
        Object caps = item.get("capabilities");
        if (caps instanceof Map<?, ?> map) {
            return map.containsKey(capability.toLowerCase()) || map.containsKey(capability.toUpperCase()) || map.containsKey(capability);
        }
        if (caps instanceof List<?> list) {
            return list.stream().anyMatch(c -> c.toString().equalsIgnoreCase(capability));
        }
        String itemType = extractString(item, "type", "catalogCode");
        return itemType != null && itemType.toLowerCase().contains(capability.toLowerCase());
    }

    private EvaluateCheckpointResponse evaluateItemAdded(
            CheckpointDefinitionDto checkpoint,
            List<Map<String, Object>> items,
            String nextStepId
    ) {
        Map<String, Object> target = checkpoint.target() != null ? checkpoint.target() : Map.of();
        String expectedCode = extractString(target, "equipmentCode", "catalogCode", "code", "type", "itemId");
        String expectedCapability = extractString(target, "capability");

        for (Map<String, Object> item : items) {
            boolean codeMatch = itemMatches(item, expectedCode);
            boolean capMatch = itemHasCapability(item, expectedCapability);
            if (codeMatch && capMatch) {
                return new EvaluateCheckpointResponse(true, null, nextStepId);
            }
        }

        return new EvaluateCheckpointResponse(false, "Required equipment or container is not present in workspace", null);
    }

    @SuppressWarnings("unchecked")
    private EvaluateCheckpointResponse evaluateMaterialAdded(
            CheckpointDefinitionDto checkpoint,
            List<Map<String, Object>> items,
            String nextStepId
    ) {
        Map<String, Object> target = checkpoint.target() != null ? checkpoint.target() : Map.of();
        Map<String, Object> params = checkpoint.parameters() != null ? checkpoint.parameters() : Map.of();

        String expectedMaterial = extractString(target, "materialCode", "formula", "reagent", "materialId");
        Double minVolume = extractDouble(params, "minVolumeMl", "minVolume", "minMl", "volumeMl");
        Double maxVolume = extractDouble(params, "maxVolumeMl", "maxVolume", "maxMl");

        for (Map<String, Object> item : items) {
            Object contentsObj = item.get("contents");
            if (contentsObj == null) {
                contentsObj = item.get("materials");
            }

            if (contentsObj instanceof List<?> list) {
                for (Object mat : list) {
                    if (mat instanceof Map<?, ?> m) {
                        Map<String, Object> matMap = (Map<String, Object>) m;
                        String matCode = extractString(matMap, "formula", "code", "materialCode", "name", "id");
                        Double volume = extractDouble(matMap, "volumeMl", "volume", "amount", "volume_ml");
                        if (materialMatches(matCode, expectedMaterial)) {
                            if (volumeInRange(volume, minVolume, maxVolume)) {
                                return new EvaluateCheckpointResponse(true, null, nextStepId);
                            }
                        }
                    }
                }
            } else if (contentsObj instanceof Map<?, ?> m) {
                Map<String, Object> matMap = (Map<String, Object>) m;
                String matCode = extractString(matMap, "formula", "code", "materialCode", "name");
                Double volume = extractDouble(matMap, "volumeMl", "volume", "totalVolume");
                if (materialMatches(matCode, expectedMaterial) && volumeInRange(volume, minVolume, maxVolume)) {
                    return new EvaluateCheckpointResponse(true, null, nextStepId);
                }
            }
        }

        if (items.isEmpty()) {
            return new EvaluateCheckpointResponse(false, "No containers available on canvas", null);
        }

        return new EvaluateCheckpointResponse(false, "Required material has not been added to the container", null);
    }

    private boolean materialMatches(String actualCode, String expectedCode) {
        if (expectedCode == null || expectedCode.isBlank()) return true;
        if (actualCode == null) return false;
        return actualCode.equalsIgnoreCase(expectedCode) || actualCode.toLowerCase().contains(expectedCode.toLowerCase());
    }

    private boolean volumeInRange(Double volume, Double min, Double max) {
        if (volume == null) return min == null;
        if (min != null && volume < min) return false;
        if (max != null && volume > max) return false;
        return true;
    }

    private EvaluateCheckpointResponse evaluateMeasurement(
            CheckpointDefinitionDto checkpoint,
            String workspaceId,
            List<Map<String, Object>> items,
            String nextStepId
    ) {
        Map<String, Object> params = checkpoint.parameters() != null ? checkpoint.parameters() : Map.of();
        Double minValue = extractDouble(params, "minValue", "min", "minTemperature", "minPh");
        Double maxValue = extractDouble(params, "maxValue", "max", "maxTemperature", "maxPh");
        String sensorType = extractString(params, "sensorType", "type", "measurementType");

        List<MeasurementEntity> measurements = measurementRepository.findByWorkspaceIdOrderByRecordedAtAsc(
                workspaceId, org.springframework.data.domain.PageRequest.of(0, 50));
        for (MeasurementEntity m : measurements) {
            if (sensorType == null || (m.getKind() != null && m.getKind().equalsIgnoreCase(sensorType))) {
                if (m.getValue() != null) {
                    double val = m.getValue().doubleValue();
                    if ((minValue == null || val >= minValue) && (maxValue == null || val <= maxValue)) {
                        return new EvaluateCheckpointResponse(true, null, nextStepId);
                    }
                }
            }
        }

        for (Map<String, Object> item : items) {
            Double val = extractDouble(item, "temperature", "temp", "measuredValue", "value", "pH", "ph");
            if (val != null) {
                if ((minValue == null || val >= minValue) && (maxValue == null || val <= maxValue)) {
                    return new EvaluateCheckpointResponse(true, null, nextStepId);
                }
            }
        }

        return new EvaluateCheckpointResponse(false, "Required measurement condition has not been met", null);
    }

    private EvaluateCheckpointResponse evaluateReactionCompleted(
            CheckpointDefinitionDto checkpoint,
            List<Map<String, Object>> items,
            String nextStepId
    ) {
        Map<String, Object> target = checkpoint.target() != null ? checkpoint.target() : Map.of();
        String expectedProduct = extractString(target, "productFormula", "product", "materialCode");

        for (Map<String, Object> item : items) {
            Object contentsObj = item.get("contents");
            if (contentsObj instanceof List<?> list) {
                for (Object c : list) {
                    if (c instanceof Map<?, ?> m) {
                        String code = extractString((Map<String, Object>) m, "formula", "code", "name");
                        if (code != null && expectedProduct != null && code.toLowerCase().contains(expectedProduct.toLowerCase())) {
                            return new EvaluateCheckpointResponse(true, null, nextStepId);
                        }
                    }
                }
            }
        }

        return new EvaluateCheckpointResponse(false, "Reaction has not produced expected result", null);
    }

    private EvaluateCheckpointResponse evaluateGenericFact(
            CheckpointDefinitionDto checkpoint,
            List<Map<String, Object>> items,
            List<Map<String, Object>> connections,
            String nextStepId
    ) {
        if (!items.isEmpty() || !connections.isEmpty()) {
            return new EvaluateCheckpointResponse(true, null, nextStepId);
        }
        return new EvaluateCheckpointResponse(false, "Laboratory state is empty", null);
    }

    private List<Map<String, Object>> parseJsonList(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String extractString(Map<String, Object> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null && !val.toString().isBlank()) {
                return val.toString();
            }
        }
        return null;
    }

    private Double extractDouble(Map<String, Object> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val instanceof Number n) {
                return n.doubleValue();
            }
            if (val != null) {
                try {
                    return Double.parseDouble(val.toString());
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }
}

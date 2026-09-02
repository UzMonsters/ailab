package com.ailab.workspace.service;

import com.ailab.chemistry.api.LaboratoryProcessService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.SessionLifecyclePayload;
import com.ailab.chemistry.domain.laboratoryevent.StepStartedPayload;
import com.ailab.chemistry.domain.laboratoryprocess.*;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.simulationengine.*;
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.workspace.domain.*;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceStateRepository stateRepository;
    private final WorkspaceEventRepository eventRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceMemberService memberService;
    private final WorkspaceScienceAuthorityService scienceAuthority;
    private final WorkspaceScienceOrchestrator scienceOrchestrator;
    private final LaboratoryProcessService processService;
    private final SimulationSessionService sessionService;
    private final ObjectMapper objectMapper;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceStateRepository stateRepository,
            WorkspaceEventRepository eventRepository,
            WorkspaceMemberRepository memberRepository,
            WorkspaceMemberService memberService,
            WorkspaceScienceAuthorityService scienceAuthority,
            WorkspaceScienceOrchestrator scienceOrchestrator,
            LaboratoryProcessService processService,
            SimulationSessionService sessionService,
            ObjectMapper objectMapper) {
        this.workspaceRepository = workspaceRepository;
        this.stateRepository = stateRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.scienceAuthority = scienceAuthority;
        this.scienceOrchestrator = scienceOrchestrator;
        this.processService = processService;
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public WorkspacePageResponse<WorkspaceDetails> listWorkspaces(
            String ownerId, String science, String search, String sortStr, int page, int size, Boolean includeDeleted) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        if (sortStr != null && !sortStr.isBlank()) {
            String[] parts = sortStr.split(",");
            if (parts.length > 0) {
                String field = parts[0].trim();
                Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;
                sort = Sort.by(direction, field);
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        boolean incDel = Boolean.TRUE.equals(includeDeleted);

        String scienceFilter = normalizedFilter(science);
        String searchFilter = normalizedFilter(search);
        Page<WorkspaceEntity> p = workspaceRepository.findAllByOwner(
                ownerId,
                scienceFilter != null ? scienceFilter : "",
                searchFilter != null ? searchFilter : "",
                scienceFilter != null,
                searchFilter != null,
                incDel,
                pageable);
        List<WorkspaceDetails> items = p.getContent().stream().map(WorkspaceDetails::fromEntity).toList();
        return new WorkspacePageResponse<>(items, p.getNumber(), p.getSize(), p.getTotalElements());
    }

    private String normalizedFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional(readOnly = true)
    public WorkspaceDetails getWorkspace(String workspaceId, String userId) {
        memberService.requirePermission(workspaceId, userId, "READ_WORKSPACE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        WorkspacePermissionsDto perms = memberService.getPermissions(workspaceId, userId);
        return WorkspaceDetails.fromEntity(entity, perms.role(), null);
    }

    @Transactional
    public WorkspaceDetails createWorkspace(String ownerId, CreateWorkspaceRequest request) {
        String wsId = "ws_" + UUID.randomUUID().toString().substring(0, 12);
        String science = request.science() != null && !request.science().isBlank() ? request.science() : "chemistry";

        String sessionId = null;
        String expId = "exp_" + UUID.randomUUID().toString().substring(0, 12);
        try {
            SimulationState state = createRunningWorkspaceSession(expId, wsId);
            sessionId = state.sessionId().value();
        } catch (Exception e) {
            sessionId = expId;
        }

        WorkspaceEntity entity = new WorkspaceEntity(wsId, ownerId, request.name(), science, sessionId);
        workspaceRepository.save(entity);

        WorkspaceStateEntity stateEntity = new WorkspaceStateEntity(wsId, 1);
        stateRepository.save(stateEntity);

        // Record OWNER membership
        WorkspaceMemberEntity member = new WorkspaceMemberEntity(wsId, ownerId, "OWNER");
        memberRepository.save(member);

        return WorkspaceDetails.fromEntity(entity, "OWNER", null);
    }

    private SimulationState createRunningWorkspaceSession(String sessionId, String workspaceId) {
        String processCode = "WORKSPACE_PROCESS_" + workspaceId;
        LaboratoryProcessDefinition published = processService.publish(processService.create(workspaceProcess(processCode)));
        var simulationSessionId = new SimulationSessionId(sessionId);
        SimulationState created = sessionService.createSession(new CreateSimulationSessionRequest(
                simulationSessionId,
                published.code(),
                published.version().value(),
                Instant.now()
        ));
        SimulationState running = sessionService.appendEvent(simulationSessionId, created.version().value(),
                new IdempotencyKey("workspace-start-" + sessionId), new SessionLifecyclePayload("workspace-created"));
        return sessionService.appendEvent(simulationSessionId, running.version().value(),
                new IdempotencyKey("workspace-react-step-" + sessionId), new StepStartedPayload("react", List.of()));
    }

    private LaboratoryProcessDefinition workspaceProcess(String processCode) {
        return new LaboratoryProcessDefinition(processCode, new LaboratoryProcessVersion(1), LaboratoryProcessStatus.DRAFT, List.of(
                new LaboratoryProcessStep(
                        new ProcessStepId("react"),
                        ProcessStepType.MIX,
                        false,
                        Duration.of("1", DurationUnit.SECOND),
                        List.of(),
                        List.of(new ProcessMaterialRequirement("workspace-material", "COMP-H2O", BigDecimal.ONE, "mL", "LIQUID", true, true)),
                        List.of(new ProcessEquipmentRequirement("workspace-equipment", "EQ-DWK-KIMAX-28014B-100-VOLUMETRIC", false)),
                        List.of(new ProcessContainerRequirement("workspace-container", "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                                BigDecimal.ONE, false, "COMP-H2O", "LIQUID")),
                        List.of(),
                        List.of("INLET"),
                        List.of("OUTLET"),
                        List.of(
                                operation(SimulationOperationType.BOOKKEEPING_MIX, "bookkeeping", "WORKSPACE_CANVAS"),
                                operation(SimulationOperationType.THERMAL_OPERATION, "calorimetry", "SENSIBLE_HEATING"),
                                operation(SimulationOperationType.PHASE_TRANSITION, "phase-behavior", "WORKSPACE_PHASE")
                        ))));
    }

    private ScientificOperationSpecification operation(SimulationOperationType operationType, String method, String reference) {
        return new ScientificOperationSpecification(operationType, new ScientificModelSelection(
                method,
                reference,
                new ScientificModelReference(method + "-workspace-model", "1.0.0"),
                List.of(new ScientificDatasetReference(method + "-workspace-dataset", "1.0.0")),
                Map.of("source", "workspace")));
    }

    @Transactional
    public WorkspaceDetails updateWorkspace(String workspaceId, String userId, UpdateWorkspaceRequest request) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (request.resolvedExpectedVersion() != null && request.resolvedExpectedVersion() != entity.getStateVersion()) {
            throw new VersionConflictException(request.resolvedExpectedVersion(), entity.getStateVersion());
        }

        if (request.name() != null && !request.name().isBlank()) {
            entity.setName(request.name().trim());
        }
        if (request.isFavorite() != null) {
            entity.setFavorite(request.isFavorite());
        }
        if (request.isDeleted() != null) {
            entity.setDeleted(request.isDeleted());
        }
        if (request.thumbnail() != null) {
            entity.setThumbnail(request.thumbnail());
        }

        entity.setStateVersion(entity.getStateVersion() + 1);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return WorkspaceDetails.fromEntity(entity);
    }

    @Transactional
    public WorkspaceDetails duplicateWorkspace(String sourceWorkspaceId, String ownerId, DuplicateWorkspaceRequest request) {
        WorkspaceEntity source = workspaceRepository.findById(sourceWorkspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(sourceWorkspaceId));

        String newWsId = "ws_" + UUID.randomUUID().toString().substring(0, 12);
        String name = request.name() != null && !request.name().isBlank()
                ? request.name().trim()
                : source.getName() + " (Copy)";

        String newSessionId = null;
        String expId = "exp_" + UUID.randomUUID().toString().substring(0, 12);
        try {
            SimulationState state = createRunningWorkspaceSession(expId, newWsId);
            newSessionId = state.sessionId().value();
        } catch (Exception e) {
            newSessionId = expId;
        }

        WorkspaceEntity duplicated = new WorkspaceEntity(newWsId, ownerId, name, source.getScience(), newSessionId);
        duplicated.setThumbnail(source.getThumbnail());
        duplicated.setStateVersion(1);
        workspaceRepository.save(duplicated);

        WorkspaceMemberEntity member = new WorkspaceMemberEntity(newWsId, ownerId, "OWNER");
        memberRepository.save(member);

        WorkspaceStateEntity sourceState = stateRepository.findById(sourceWorkspaceId)
                .orElse(new WorkspaceStateEntity(sourceWorkspaceId, 1));
        WorkspaceStateEntity newState = new WorkspaceStateEntity(newWsId, 1);
        newState.setItemsJson(sourceState.getItemsJson());
        newState.setConnectionsJson(sourceState.getConnectionsJson());
        newState.setViewportJson(sourceState.getViewportJson());
        newState.setGridJson(sourceState.getGridJson());
        stateRepository.save(newState);

        return WorkspaceDetails.fromEntity(duplicated, "OWNER", null);
    }

    @Transactional
    public void deleteWorkspace(String workspaceId, String userId) {
        memberService.requirePermission(workspaceId, userId, "MANAGE_WORKSPACE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        workspaceRepository.delete(entity);
    }

    @Transactional
    public WorkspaceDetails restoreWorkspace(String workspaceId, String userId) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        entity.setDeleted(false);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return WorkspaceDetails.fromEntity(entity);
    }

    @Transactional
    public Map<String, Object> updateThumbnail(String workspaceId, String userId, ThumbnailRequest request) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        String thumb = request.imageData() != null && !request.imageData().isBlank() ? request.imageData() : request.svg();
        entity.setThumbnail(thumb);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return Map.of("thumbnailUrl", thumb != null ? thumb : "", "updatedAt", entity.getUpdatedAt().toString());
    }

    @Transactional(readOnly = true)
    public WorkspaceStateDto getState(String workspaceId, String userId) {
        memberService.requirePermission(workspaceId, userId, "READ_WORKSPACE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        WorkspaceStateEntity state = stateRepository.findById(workspaceId)
                .orElseGet(() -> new WorkspaceStateEntity(workspaceId, entity.getStateVersion()));
        return buildStateDto(entity, state);
    }

    @Transactional
    public WorkspaceStateDto saveState(String workspaceId, String userId, Long expectedVersion, WorkspaceStateDto incomingState) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (expectedVersion != null && expectedVersion != entity.getStateVersion()) {
            throw new VersionConflictException(expectedVersion, entity.getStateVersion());
        }

        long nextVersion = entity.getStateVersion() + 1;
        entity.setStateVersion(nextVersion);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);

        WorkspaceStateEntity stateEntity = stateRepository.findById(workspaceId)
                .orElseGet(() -> new WorkspaceStateEntity(workspaceId, nextVersion));

        stateEntity.setStateVersion(nextVersion);
        stateEntity.setItemsJson(toJson(incomingState.items()));
        stateEntity.setConnectionsJson(toJson(incomingState.connections()));
        stateEntity.setViewportJson(toJson(incomingState.viewport()));
        stateEntity.setGridJson(toJson(incomingState.grid()));
        stateEntity.setUpdatedAt(Instant.now());
        stateRepository.save(stateEntity);

        String eventId = "evt_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceEventEntity evt = new WorkspaceEventEntity(
                eventId, workspaceId, userId, "snapshot-" + nextVersion, "SNAPSHOT_SAVED", nextVersion, toJson(incomingState)
        );
        eventRepository.save(evt);

        return buildStateDto(entity, stateEntity);
    }

    @Transactional
    public WorkspaceEventAck appendEvent(String workspaceId, String userId, SandboxEventCommand cmd) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        String clientEventId = cmd.clientEventId() != null ? cmd.clientEventId() : UUID.randomUUID().toString();

        Optional<WorkspaceEventEntity> existing = eventRepository.findByWorkspaceIdAndUserIdAndClientEventId(
                workspaceId, userId, clientEventId);
        if (existing.isPresent()) {
            WorkspaceEventEntity evt = existing.get();
            if (!Objects.equals(evt.getEventType(), cmd.eventType()) || !Objects.equals(evt.getPayloadJson(), toJson(cmd.payload()))) {
                throw new VersionConflictException(cmd.expectedVersion() != null ? cmd.expectedVersion() : entity.getStateVersion(), entity.getStateVersion());
            }
            return new WorkspaceEventAck(
                    clientEventId,
                    clientEventId,
                    evt.getId(),
                    evt.getEventType(),
                    workspaceId,
                    entity.getExperimentSessionId(),
                    true,
                    evt.getVersion(),
                    evt.getVersion(),
                    Map.of("idempotencyHit", true),
                    List.of(),
                    List.of(),
                    List.of(),
                    evt.getCreatedAt().toString()
            );
        }

        long expected = cmd.expectedVersion() != null ? cmd.expectedVersion() : entity.getStateVersion();
        if (expected != entity.getStateVersion()) {
            throw new VersionConflictException(expected, entity.getStateVersion());
        }

        long newVersion = entity.getStateVersion() + 1;
        entity.setStateVersion(newVersion);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);

        WorkspaceStateEntity stateEntity = stateRepository.findById(workspaceId)
                .orElseGet(() -> new WorkspaceStateEntity(workspaceId, newVersion));

        WorkspaceScienceOrchestrator.ScientificExecutionOutcome outcome = applyEventToState(
                stateEntity, entity, cmd.eventType(), cmd.payload(), clientEventId);

        stateEntity.setStateVersion(newVersion);
        stateEntity.setUpdatedAt(Instant.now());
        stateRepository.save(stateEntity);

        String eventId = "evt_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceEventEntity eventEntity = new WorkspaceEventEntity(
                eventId, workspaceId, userId, clientEventId, cmd.eventType(), newVersion, toJson(cmd.payload())
        );
        eventRepository.save(eventEntity);

        Map<String, Object> stateDelta = new LinkedHashMap<>();
        stateDelta.put("itemsChanged", outcome.mutatedItems());
        stateDelta.put("connectionsChanged", outcome.mutatedConnections());

        if ("ITEM_ADDED".equalsIgnoreCase(cmd.eventType()) && !outcome.mutatedItems().isEmpty()) {
            stateDelta.put("addedItem", outcome.mutatedItems().get(outcome.mutatedItems().size() - 1));
        } else if ("ITEM_DELETED".equalsIgnoreCase(cmd.eventType())) {
            stateDelta.put("deletedItemId", cmd.payload().get("itemId"));
        } else if (("MATERIAL_ADDED".equalsIgnoreCase(cmd.eventType()) || "MEASURE".equalsIgnoreCase(cmd.eventType()) || "ITEM_MOVED".equalsIgnoreCase(cmd.eventType()) || "ITEM_RESIZED".equalsIgnoreCase(cmd.eventType())) && !outcome.mutatedItems().isEmpty()) {
            String itemId = String.valueOf(cmd.payload().getOrDefault("itemId", cmd.payload().get("targetItemId")));
            for (Map<String, Object> item : outcome.mutatedItems()) {
                if (itemId.equals(item.get("id"))) {
                    stateDelta.put("updatedItem", item);
                    break;
                }
            }
            if (!stateDelta.containsKey("updatedItem") && !outcome.mutatedItems().isEmpty()) {
                stateDelta.put("updatedItem", outcome.mutatedItems().get(0));
            }
        } else if (("POUR".equalsIgnoreCase(cmd.eventType()) || "POUR_COMPLETED".equalsIgnoreCase(cmd.eventType())) && !outcome.mutatedItems().isEmpty()) {
            String sId = String.valueOf(cmd.payload().get("sourceId"));
            String tId = String.valueOf(cmd.payload().get("targetId"));
            for (Map<String, Object> item : outcome.mutatedItems()) {
                if (sId.equals(item.get("id"))) stateDelta.put("sourceItem", item);
                if (tId.equals(item.get("id"))) stateDelta.put("targetItem", item);
            }
        } else if ("CONNECT".equalsIgnoreCase(cmd.eventType())) {
            stateDelta.put("addedConnection", cmd.payload());
        } else if ("DISCONNECT".equalsIgnoreCase(cmd.eventType())) {
            stateDelta.put("disconnectedId", cmd.payload().get("connectionId"));
        }

        return new WorkspaceEventAck(
                clientEventId,
                clientEventId,
                eventId,
                cmd.eventType(),
                workspaceId,
                entity.getExperimentSessionId(),
                true,
                newVersion,
                newVersion,
                stateDelta,
                outcome.measurements(),
                outcome.safetyWarnings(),
                outcome.checkpointFacts(),
                Instant.now().toString()
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEvents(String workspaceId, String userId, Long afterVersion, Integer limit) {
        memberService.requirePermission(workspaceId, userId, "READ_WORKSPACE");
        workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        long after = afterVersion != null ? afterVersion : 0;
        List<WorkspaceEventEntity> list = eventRepository.findByWorkspaceIdAndVersionGreaterThanOrderByVersionAsc(workspaceId, after);
        if (limit != null && limit > 0 && list.size() > limit) {
            list = list.subList(0, limit);
        }

        return list.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("eventId", e.getId());
            m.put("workspaceId", e.getWorkspaceId());
            m.put("userId", e.getUserId());
            m.put("clientEventId", e.getClientEventId());
            m.put("eventType", e.getEventType());
            m.put("version", e.getVersion());
            m.put("payload", fromJson(e.getPayloadJson(), new TypeReference<Map<String, Object>>() {}));
            m.put("createdAt", e.getCreatedAt().toString());
            return m;
        }).toList();
    }

    @Transactional
    public WorkspaceStateDto undo(String workspaceId, String userId, Long expectedVersion) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        checkExpected(expectedVersion, entity);

        List<WorkspaceEventEntity> events = eventRepository.findByWorkspaceIdOrderByVersionAsc(workspaceId);
        Set<String> undone = undoneEventIds(events);
        WorkspaceEventEntity target = events.stream()
                .filter(e -> isReversible(e.getEventType()))
                .filter(e -> !undone.contains(e.getId()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No reversible workspace event is available to undo"));

        Set<String> skipped = undoneEventIds(events);
        skipped.add(target.getId());
        WorkspaceStateEntity replayed = replayWorkspaceState(workspaceId, events, skipped);
        long nextVersion = entity.getStateVersion() + 1;
        persistReplayState(entity, replayed, nextVersion);
        appendHistoryEvent(workspaceId, userId, "undo-" + target.getId(), "UNDO", nextVersion, Map.of("undoneEventId", target.getId()));
        return buildStateDto(entity, replayed);
    }

    @Transactional
    public WorkspaceStateDto redo(String workspaceId, String userId, Long expectedVersion) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        checkExpected(expectedVersion, entity);

        List<WorkspaceEventEntity> events = eventRepository.findByWorkspaceIdOrderByVersionAsc(workspaceId);
        Optional<String> redoTarget = latestRedoTarget(events);
        if (redoTarget.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No workspace event is available to redo");
        }

        long nextVersion = entity.getStateVersion() + 1;
        Set<String> skipped = undoneEventIds(events);
        skipped.remove(redoTarget.get());
        WorkspaceStateEntity replayed = replayWorkspaceState(workspaceId, events, skipped);
        persistReplayState(entity, replayed, nextVersion);
        appendHistoryEvent(workspaceId, userId, "redo-" + redoTarget.get(), "REDO", nextVersion, Map.of("redoneEventId", redoTarget.get()));
        return buildStateDto(entity, replayed);
    }

    @Transactional
    public Map<String, Object> publishWorkspace(String workspaceId, String userId, PublishWorkspaceRequest request) {
        memberService.requirePermission(workspaceId, userId, "MANAGE_WORKSPACE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        return Map.of(
                "workspaceId", workspaceId,
                "shareUrl", "https://ailab.app/shared-workspaces/" + workspaceId
        );
    }

    @Transactional
    public Map<String, Object> autosave(String workspaceId, String userId, AutosaveRequest request) {
        memberService.requirePermission(workspaceId, userId, "EDIT_SCENE");
        WorkspaceEntity entity = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (request.expectedVersion() != null && request.expectedVersion() != entity.getStateVersion()) {
            throw new VersionConflictException(request.expectedVersion(), entity.getStateVersion());
        }

        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return Map.of("stateVersion", entity.getStateVersion(), "savedAt", entity.getUpdatedAt().toString());
    }

    private WorkspaceScienceOrchestrator.ScientificExecutionOutcome applyEventToState(
            WorkspaceStateEntity stateEntity,
            WorkspaceEntity workspace,
            String eventType,
            Map<String, Object> payload,
            String clientEventId
    ) {
        if (payload == null) payload = Map.of();

        List<Map<String, Object>> items = fromJson(stateEntity.getItemsJson(), new TypeReference<List<Map<String, Object>>>() {});
        if (items == null) items = new ArrayList<>();
        else items = new ArrayList<>(items);

        List<Map<String, Object>> connections = fromJson(stateEntity.getConnectionsJson(), new TypeReference<List<Map<String, Object>>>() {});
        if (connections == null) connections = new ArrayList<>();
        else connections = new ArrayList<>(connections);

        if ("ITEM_ADDED".equalsIgnoreCase(eventType)) {
            require(payload, "id");
            require(payload, "equipmentType");
            Map<String, Object> authoritativeItem = scienceAuthority.authoritativeEquipment(new HashMap<>(payload));
            items.add(authoritativeItem);
        } else if ("ITEM_MOVED".equalsIgnoreCase(eventType) || "ITEM_RESIZED".equalsIgnoreCase(eventType) || "ITEM_ROTATED".equalsIgnoreCase(eventType)) {
            String itemId = (String) payload.get("itemId");
            if (itemId != null) {
                for (Map<String, Object> it : items) {
                    if (itemId.equals(it.get("id"))) {
                        it.putAll(payload);
                        break;
                    }
                }
            }
        } else if ("ITEM_DELETED".equalsIgnoreCase(eventType)) {
            String itemId = (String) payload.get("itemId");
            if (itemId != null) {
                items.removeIf(it -> itemId.equals(it.get("id")));
                connections.removeIf(conn -> itemId.equals(conn.get("fromItemId")) || itemId.equals(conn.get("toItemId")));
            }
        } else if ("MATERIAL_ADDED".equalsIgnoreCase(eventType)) {
            String materialId = require(payload, "materialId");
            scienceAuthority.requireKnownMaterial(materialId);
        } else if ("CONNECT".equalsIgnoreCase(eventType)) {
            validateConnection(items, payload);
        }

        // Run scientific orchestration (covers chemical reactions, dissolution, dilution, heating, measurements, checkpoints)
        WorkspaceScienceOrchestrator.ScientificExecutionOutcome outcome = scienceOrchestrator.processOperation(
                workspace != null ? workspace.getId() : stateEntity.getWorkspaceId(),
                workspace != null ? workspace.getExperimentSessionId() : "sess_01",
                eventType,
                payload,
                items,
                connections
        );

        stateEntity.setItemsJson(toJson(outcome.mutatedItems()));
        stateEntity.setConnectionsJson(toJson(outcome.mutatedConnections()));
        return outcome;
    }

    private void checkExpected(Long expectedVersion, WorkspaceEntity entity) {
        if (expectedVersion != null && expectedVersion != entity.getStateVersion()) {
            throw new VersionConflictException(expectedVersion, entity.getStateVersion());
        }
    }

    private boolean isReversible(String eventType) {
        return !Set.of("UNDO", "REDO", "SNAPSHOT_SAVED").contains(eventType.toUpperCase(Locale.ROOT));
    }

    private Set<String> undoneEventIds(List<WorkspaceEventEntity> events) {
        Set<String> undone = new HashSet<>();
        for (WorkspaceEventEntity event : events) {
            Map<String, Object> payload = fromJson(event.getPayloadJson(), new TypeReference<Map<String, Object>>() {});
            if ("UNDO".equalsIgnoreCase(event.getEventType())) {
                String id = string(payload == null ? null : payload.get("undoneEventId"));
                if (!id.isBlank()) undone.add(id);
            } else if ("REDO".equalsIgnoreCase(event.getEventType())) {
                String id = string(payload == null ? null : payload.get("redoneEventId"));
                if (!id.isBlank()) undone.remove(id);
            }
        }
        return undone;
    }

    private Optional<String> latestRedoTarget(List<WorkspaceEventEntity> events) {
        Set<String> undone = undoneEventIds(events);
        for (int i = events.size() - 1; i >= 0; i--) {
            WorkspaceEventEntity event = events.get(i);
            if (undone.contains(event.getId())) {
                return Optional.of(event.getId());
            }
        }
        return Optional.empty();
    }

    private WorkspaceStateEntity replayWorkspaceState(String workspaceId, List<WorkspaceEventEntity> events, Set<String> skippedEventIds) {
        WorkspaceStateEntity replay = new WorkspaceStateEntity(workspaceId, 1);
        for (WorkspaceEventEntity event : events) {
            if (skippedEventIds.contains(event.getId()) || "UNDO".equalsIgnoreCase(event.getEventType()) || "REDO".equalsIgnoreCase(event.getEventType())) {
                continue;
            }
            Map<String, Object> payload = fromJson(event.getPayloadJson(), new TypeReference<Map<String, Object>>() {});
            applyEventToState(replay, null, event.getEventType(), payload, event.getClientEventId());
        }
        return replay;
    }

    private void persistReplayState(WorkspaceEntity entity, WorkspaceStateEntity replayed, long nextVersion) {
        entity.setStateVersion(nextVersion);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        replayed.setStateVersion(nextVersion);
        replayed.setUpdatedAt(Instant.now());
        stateRepository.save(replayed);
    }

    private void appendHistoryEvent(String workspaceId, String userId, String clientEventId, String eventType, long version, Map<String, Object> payload) {
        String eventId = "evt_" + UUID.randomUUID().toString().substring(0, 12);
        eventRepository.save(new WorkspaceEventEntity(eventId, workspaceId, userId, clientEventId, eventType, version, toJson(payload)));
    }

    private void validateConnection(List<Map<String, Object>> items, Map<String, Object> payload) {
        String fromId = string(payload.getOrDefault("sourceItemId", payload.get("fromItemId")));
        String toId = string(payload.getOrDefault("targetItemId", payload.get("toItemId")));
        String fromPort = string(payload.getOrDefault("sourcePort", payload.get("fromPortId")));
        String toPort = string(payload.getOrDefault("targetPort", payload.get("toPortId")));

        Map<String, Object> source = itemById(items, fromId);
        Map<String, Object> target = itemById(items, toId);

        scienceAuthority.requireKnownPort(fromPort);
        scienceAuthority.requireKnownPort(toPort);
        scienceAuthority.validateConnection(source, fromPort, target, toPort);
    }

    private Map<String, Object> itemById(List<Map<String, Object>> items, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("item id is required");
        }
        for (Map<String, Object> item : items) {
            if (itemId.equals(item.get("id"))) {
                return item;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unknown workspace item: " + itemId);
    }

    private String require(Map<String, Object> payload, String field) {
        String value = string(payload.get(field));
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    private WorkspaceStateDto buildStateDto(WorkspaceEntity entity, WorkspaceStateEntity state) {
        Map<String, Object> vp = fromJson(state.getViewportJson(), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> grid = fromJson(state.getGridJson(), new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> items = fromJson(state.getItemsJson(), new TypeReference<List<Map<String, Object>>>() {});
        List<Map<String, Object>> connections = fromJson(state.getConnectionsJson(), new TypeReference<List<Map<String, Object>>>() {});
        List<Map<String, Object>> log = fromJson(state.getLogJson(), new TypeReference<List<Map<String, Object>>>() {});

        return new WorkspaceStateDto(
                entity.getId(),
                entity.getExperimentSessionId(),
                entity.getStateVersion(),
                vp != null ? vp : Map.of("x", 0, "y", 0, "zoom", 1),
                grid != null ? grid : Map.of("enabled", true, "size", 20, "snap", true),
                items != null ? items : List.of(),
                connections != null ? connections : List.of(),
                log != null ? log : List.of(),
                state.getUpdatedAt().toString()
        );
    }

    private String string(Object value) {
        return value == null ? "" : value.toString();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize to JSON", e);
        }
    }

    private <T> T fromJson(String json, TypeReference<T> type) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }
}

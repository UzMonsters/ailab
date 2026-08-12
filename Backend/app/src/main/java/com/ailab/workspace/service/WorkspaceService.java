package com.ailab.workspace.service;

import com.ailab.chemistry.api.LaboratoryProcessService;
import com.ailab.chemistry.api.SimulationSessionService;
import com.ailab.chemistry.domain.laboratoryevent.IdempotencyKey;
import com.ailab.chemistry.domain.laboratoryevent.MaterialDispensedPayload;
import com.ailab.chemistry.domain.laboratoryevent.MaterialTransferredPayload;
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
import com.ailab.chemistry.domain.simulationstate.CreateSimulationSessionRequest;
import com.ailab.chemistry.domain.simulationstate.SimulationSessionId;
import com.ailab.chemistry.domain.simulationstate.SimulationState;
import com.ailab.chemistry.domain.simulationengine.ScientificDatasetReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelReference;
import com.ailab.chemistry.domain.simulationengine.ScientificModelSelection;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.ailab.chemistry.domain.simulationengine.SimulationOperationType;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspaceEventEntity;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.exception.VersionConflictException;
import com.ailab.workspace.exception.WorkspaceNotFoundException;
import com.ailab.workspace.repository.WorkspaceEventRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import com.ailab.workspace.repository.WorkspaceStateRepository;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceStateRepository stateRepository;
    private final WorkspaceEventRepository eventRepository;
    private final LaboratoryProcessService processService;
    private final SimulationSessionService sessionService;
    private final WorkspaceScienceAuthorityService scienceAuthority;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate requiresNewTransaction;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceStateRepository stateRepository,
            WorkspaceEventRepository eventRepository,
            LaboratoryProcessService processService,
            SimulationSessionService sessionService,
            WorkspaceScienceAuthorityService scienceAuthority,
            ObjectMapper objectMapper,
            PlatformTransactionManager transactionManager) {
        this.workspaceRepository = workspaceRepository;
        this.stateRepository = stateRepository;
        this.eventRepository = eventRepository;
        this.processService = processService;
        this.sessionService = sessionService;
        this.scienceAuthority = scienceAuthority;
        this.objectMapper = objectMapper;
        this.requiresNewTransaction = new TransactionTemplate(transactionManager);
        this.requiresNewTransaction.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional(readOnly = true)
    public WorkspacePageResponse<WorkspaceDetails> listWorkspaces(
            String ownerId, String science, String search, String sortStr, int page, int size, Boolean includeDeleted) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        if (sortStr != null && !sortStr.isBlank()) {
            if (sortStr.equalsIgnoreCase("name,asc")) sort = Sort.by(Sort.Direction.ASC, "name");
            else if (sortStr.equalsIgnoreCase("name,desc")) sort = Sort.by(Sort.Direction.DESC, "name");
            else if (sortStr.equalsIgnoreCase("createdAt,asc")) sort = Sort.by(Sort.Direction.ASC, "createdAt");
            else if (sortStr.equalsIgnoreCase("createdAt,desc")) sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        boolean incDel = includeDeleted != null && includeDeleted;
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
    public WorkspaceDetails getWorkspace(String workspaceId, String ownerId) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        return WorkspaceDetails.fromEntity(entity);
    }

    @Transactional
    public WorkspaceDetails createWorkspace(String ownerId, CreateWorkspaceRequest request) {
        String wsId = "ws_" + UUID.randomUUID().toString().substring(0, 12);
        String science = request.science() != null && !request.science().isBlank() ? request.science() : "chemistry";

        String sessionId = null;
        String expId = "exp_" + UUID.randomUUID().toString().substring(0, 12);
        try {
            SimulationState state = requiresNewTransaction.execute(status -> createRunningWorkspaceSession(expId, wsId));
            sessionId = state.sessionId().value();
        } catch (Exception e) {
            sessionId = expId;
        }

        WorkspaceEntity entity = new WorkspaceEntity(wsId, ownerId, request.name(), science, sessionId);
        workspaceRepository.save(entity);

        WorkspaceStateEntity stateEntity = new WorkspaceStateEntity(wsId, 1);
        stateRepository.save(stateEntity);

        return WorkspaceDetails.fromEntity(entity);
    }

    private SimulationState createRunningWorkspaceSession(String sessionId, String workspaceId) {
        String processCode = "WORKSPACE_PROCESS_" + workspaceId;
        LaboratoryProcessDefinition published = processService.publish(processService.create(workspaceProcess(processCode)));
        var simulationSessionId = new com.ailab.chemistry.domain.simulationstate.SimulationSessionId(sessionId);
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
                        List.<ProcessStepDependency>of(),
                        List.of(new ProcessMaterialRequirement("workspace-material", "COMP-H2O", java.math.BigDecimal.ONE, "mL", "LIQUID", true, true)),
                        List.of(new ProcessEquipmentRequirement("workspace-equipment", "EQ-DWK-KIMAX-28014B-100-VOLUMETRIC", false)),
                        List.of(new ProcessContainerRequirement("workspace-container", "CON-DWK-KIMAX-28014B-100-VOLUMETRIC",
                                java.math.BigDecimal.ONE, false, "COMP-H2O", "LIQUID")),
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
    public WorkspaceDetails updateWorkspace(String workspaceId, String ownerId, UpdateWorkspaceRequest request) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (request.stateVersion() != null && request.stateVersion() != entity.getStateVersion()) {
            throw new VersionConflictException(request.stateVersion(), entity.getStateVersion());
        }

        if (request.name() != null && !request.name().isBlank()) {
            entity.setName(request.name());
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

        stateRepository.findById(workspaceId).ifPresent(st -> {
            st.setStateVersion(entity.getStateVersion());
            st.setUpdatedAt(Instant.now());
            stateRepository.save(st);
        });

        return WorkspaceDetails.fromEntity(entity);
    }

    @Transactional
    public WorkspaceDetails duplicateWorkspace(String workspaceId, String ownerId, DuplicateWorkspaceRequest request) {
        WorkspaceEntity original = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        String newWsId = "ws_" + UUID.randomUUID().toString().substring(0, 12);
        String newName = (request != null && request.name() != null && !request.name().isBlank())
                ? request.name()
                : original.getName() + " (Copy)";

        String newSessionId = "exp_" + UUID.randomUUID().toString().substring(0, 12);

        WorkspaceEntity copy = new WorkspaceEntity(newWsId, ownerId, newName, original.getScience(), newSessionId);
        copy.setThumbnail(original.getThumbnail());
        workspaceRepository.save(copy);

        Optional<WorkspaceStateEntity> origState = stateRepository.findById(workspaceId);
        WorkspaceStateEntity newState = new WorkspaceStateEntity(newWsId, 1);
        origState.ifPresent(st -> {
            newState.setViewportJson(st.getViewportJson());
            newState.setGridJson(st.getGridJson());
            newState.setItemsJson(st.getItemsJson());
            newState.setConnectionsJson(st.getConnectionsJson());
            newState.setLogJson(st.getLogJson());
        });
        stateRepository.save(newState);

        return WorkspaceDetails.fromEntity(copy);
    }

    @Transactional
    public void deleteWorkspacePermanently(String workspaceId, String ownerId) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        workspaceRepository.delete(entity);
    }

    @Transactional
    public WorkspaceDetails restoreWorkspace(String workspaceId, String ownerId) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        entity.setDeleted(false);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return WorkspaceDetails.fromEntity(entity);
    }

    @Transactional
    public Map<String, Object> updateThumbnail(String workspaceId, String ownerId, ThumbnailRequest request) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        String thumb = request.imageData() != null ? request.imageData() : request.svg();
        entity.setThumbnail(thumb);
        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);
        return Map.of("thumbnailUrl", thumb != null ? thumb : "", "updatedAt", entity.getUpdatedAt().toString());
    }

    @Transactional(readOnly = true)
    public WorkspaceStateDto getWorkspaceState(String workspaceId, String ownerId) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        WorkspaceStateEntity state = stateRepository.findById(workspaceId)
                .orElseGet(() -> new WorkspaceStateEntity(workspaceId, entity.getStateVersion()));

        return buildStateDto(entity, state);
    }

    @Transactional
    public WorkspaceStateDto saveWorkspaceState(String workspaceId, String ownerId, Long expectedVersion, WorkspaceStateDto stateDto) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
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
        if (stateDto.viewport() != null) stateEntity.setViewportJson(toJson(stateDto.viewport()));
        if (stateDto.grid() != null) stateEntity.setGridJson(toJson(stateDto.grid()));
        if (stateDto.items() != null) stateEntity.setItemsJson(toJson(stateDto.items()));
        if (stateDto.connections() != null) stateEntity.setConnectionsJson(toJson(stateDto.connections()));
        if (stateDto.log() != null) stateEntity.setLogJson(toJson(stateDto.log()));
        stateEntity.setUpdatedAt(Instant.now());
        stateRepository.save(stateEntity);

        return buildStateDto(entity, stateEntity);
    }

    @Transactional
    public WorkspaceEventAck appendEvent(String workspaceId, String userId, SandboxEventCommand cmd) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, userId)
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
                    evt.getId(),
                    evt.getEventType(),
                    workspaceId,
                    entity.getExperimentSessionId(),
                    evt.getVersion(),
                    Map.of("idempotencyHit", true),
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

        Map<String, Object> stateDelta = applyEventToState(stateEntity, entity, cmd.eventType(), cmd.payload(), clientEventId, true);
        stateEntity.setStateVersion(newVersion);
        stateEntity.setUpdatedAt(Instant.now());
        stateRepository.save(stateEntity);

        String eventId = "evt_" + UUID.randomUUID().toString().substring(0, 12);
        WorkspaceEventEntity eventEntity = new WorkspaceEventEntity(
                eventId, workspaceId, userId, clientEventId, cmd.eventType(), newVersion, toJson(cmd.payload())
        );
        eventRepository.save(eventEntity);

        return new WorkspaceEventAck(
                clientEventId,
                eventId,
                cmd.eventType(),
                workspaceId,
                entity.getExperimentSessionId(),
                newVersion,
                stateDelta,
                List.of(),
                Instant.now().toString()
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEvents(String workspaceId, String ownerId, Long afterVersion, Integer limit) {
        workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
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
    public WorkspaceStateDto undo(String workspaceId, String ownerId, Long expectedVersion) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
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
        appendHistoryEvent(workspaceId, ownerId, "undo-" + target.getId(), "UNDO", nextVersion, Map.of("undoneEventId", target.getId()));
        return buildStateDto(entity, replayed);
    }

    @Transactional
    public WorkspaceStateDto redo(String workspaceId, String ownerId, Long expectedVersion) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
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
        appendHistoryEvent(workspaceId, ownerId, "redo-" + redoTarget.get(), "REDO", nextVersion, Map.of("redoneEventId", redoTarget.get()));
        return buildStateDto(entity, replayed);
    }

    @Transactional
    public Map<String, Object> publishWorkspace(String workspaceId, String ownerId, PublishWorkspaceRequest request) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        return Map.of(
                "workspaceId", workspaceId,
                "shareUrl", "https://ailab.app/s/" + workspaceId,
                "publishedAt", Instant.now().toString()
        );
    }

    @Transactional
    public Map<String, Object> autosave(String workspaceId, String ownerId, AutosaveRequest request) {
        WorkspaceEntity entity = workspaceRepository.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (request.expectedVersion() != null && request.expectedVersion() != entity.getStateVersion()) {
            throw new VersionConflictException(request.expectedVersion(), entity.getStateVersion());
        }

        entity.setUpdatedAt(Instant.now());
        workspaceRepository.save(entity);

        return Map.of("stateVersion", entity.getStateVersion(), "savedAt", entity.getUpdatedAt().toString());
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> applyEventToState(WorkspaceStateEntity stateEntity, WorkspaceEntity workspace,
                                                  String eventType, Map<String, Object> payload, String clientEventId,
                                                  boolean executeScientific) {
        Map<String, Object> delta = new HashMap<>();
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
            delta.put("addedItem", authoritativeItem);
        } else if ("ITEM_MOVED".equalsIgnoreCase(eventType) || "ITEM_RESIZED".equalsIgnoreCase(eventType) || "ITEM_ROTATED".equalsIgnoreCase(eventType)) {
            String itemId = (String) payload.get("itemId");
            if (itemId != null) {
                for (Map<String, Object> it : items) {
                    if (itemId.equals(it.get("id"))) {
                        it.putAll(payload);
                        delta.put("updatedItem", it);
                        break;
                    }
                }
            }
        } else if ("ITEM_DELETED".equalsIgnoreCase(eventType)) {
            String itemId = (String) payload.get("itemId");
            if (itemId != null) {
                items.removeIf(it -> itemId.equals(it.get("id")));
                connections.removeIf(conn -> itemId.equals(conn.get("fromItemId")) || itemId.equals(conn.get("toItemId")));
                delta.put("deletedItemId", itemId);
            }
        } else if ("CONNECT".equalsIgnoreCase(eventType)) {
            validateConnection(items, payload);
            connections.add(new HashMap<>(payload));
            delta.put("addedConnection", payload);
        } else if ("DISCONNECT".equalsIgnoreCase(eventType)) {
            String connId = (String) payload.get("connectionId");
            if (connId != null) {
                connections.removeIf(c -> connId.equals(c.get("id")));
                delta.put("disconnectedId", connId);
            }
        } else if ("MATERIAL_ADDED".equalsIgnoreCase(eventType)) {
            Map<String, Object> item = itemById(items, string(payload.get("itemId")));
            String materialId = require(payload, "materialId");
            scienceAuthority.requireKnownMaterial(materialId);
            double amount = positiveDouble(payload.get("amountMl"), "amountMl");
            double capacity = capacityMl(item);
            String phase = normalizedPhase(materialId, payload.getOrDefault("phase", "liquid"));
            double nextVolume;
            if (executeScientific && workspace != null && workspace.getExperimentSessionId() != null) {
                SimulationState authoritative = appendMaterialDispensed(workspace, clientEventId, item, materialId, amount, capacity, phase);
                nextVolume = authoritative.quantity(string(item.get("id")), materialId, "mL", phase).doubleValue();
            } else {
                nextVolume = volumeMl(item) + amount;
                if (nextVolume > capacity) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Material addition exceeds vessel capacity");
                }
            }
            item.put("materialId", materialId);
            item.put("phase", phase.toLowerCase(Locale.ROOT));
            item.put("volumeMl", nextVolume);
            item.put("liquidLevel", liquidLevel(nextVolume, capacity));
            item.putIfAbsent("temperatureC", 25.0);
            delta.put("updatedItem", item);
        } else if ("POUR".equalsIgnoreCase(eventType) || "POUR_COMPLETED".equalsIgnoreCase(eventType)) {
            Map<String, Object> source = itemById(items, string(payload.get("sourceId")));
            Map<String, Object> target = itemById(items, string(payload.get("targetId")));
            double amount = positiveDouble(payload.get("amountMl"), "amountMl");
            String materialId = string(payload.getOrDefault("materialId", source.get("materialId")));
            if (materialId == null || materialId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Source vessel has no material to transfer");
            }
            scienceAuthority.requireKnownMaterial(materialId);
            String phase = normalizedPhase(materialId, payload.getOrDefault("phase", source.getOrDefault("phase", "liquid")));
            double targetCapacity = capacityMl(target);
            double sourceNext;
            double targetNext;
            if (executeScientific && workspace != null && workspace.getExperimentSessionId() != null) {
                SimulationState authoritative = appendMaterialTransferred(workspace, clientEventId, source, target,
                        materialId, amount, targetCapacity, phase);
                sourceNext = authoritative.quantity(string(source.get("id")), materialId, "mL", phase).doubleValue();
                targetNext = authoritative.quantity(string(target.get("id")), materialId, "mL", phase).doubleValue();
            } else {
                sourceNext = volumeMl(source) - amount;
                if (sourceNext < 0) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Transfer amount exceeds source vessel contents");
                }
                targetNext = volumeMl(target) + amount;
                if (targetNext > targetCapacity) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Transfer exceeds target vessel capacity");
                }
            }
            source.put("volumeMl", sourceNext);
            source.put("liquidLevel", liquidLevel(sourceNext, capacityMl(source)));
            target.put("volumeMl", targetNext);
            target.put("liquidLevel", liquidLevel(targetNext, targetCapacity));
            source.put("phase", phase.toLowerCase(Locale.ROOT));
            target.put("materialId", materialId);
            target.put("phase", phase.toLowerCase(Locale.ROOT));
            delta.put("sourceItem", source);
            delta.put("targetItem", target);
        } else if (isEquipmentOperation(eventType)) {
            Map<String, Object> item = itemById(items, string(payload.getOrDefault("equipmentId", payload.get("itemId"))));
            scienceAuthority.authoritativeEquipment(item);
            item.put("operation", eventType.toUpperCase(Locale.ROOT));
            if (payload.containsKey("targetTemperatureC")) {
                item.put("targetTemperatureC", payload.get("targetTemperatureC"));
            }
            delta.put("updatedEquipment", item);
        } else {
            delta.put("genericEvent", payload);
        }

        stateEntity.setItemsJson(toJson(items));
        stateEntity.setConnectionsJson(toJson(connections));
        return delta;
    }

    private SimulationState appendMaterialDispensed(WorkspaceEntity workspace, String clientEventId, Map<String, Object> item,
                                                    String materialId, double amountMl, double capacityMl, String phase) {
        SimulationSessionId sessionId = new SimulationSessionId(workspace.getExperimentSessionId());
        try {
            SimulationState current = sessionService.getCurrentState(sessionId);
            return sessionService.appendEvent(sessionId, current.version().value(), new IdempotencyKey("workspace-" + clientEventId),
                    new MaterialDispensedPayload(
                            string(item.get("id")),
                            string(item.getOrDefault("containerProfileId", "")),
                            materialId,
                            BigDecimal.valueOf(amountMl),
                            "mL",
                            phase,
                            BigDecimal.valueOf(capacityMl)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex);
        }
    }

    private SimulationState appendMaterialTransferred(WorkspaceEntity workspace, String clientEventId,
                                                      Map<String, Object> source, Map<String, Object> target,
                                                      String materialId, double amountMl, double targetCapacityMl,
                                                      String phase) {
        SimulationSessionId sessionId = new SimulationSessionId(workspace.getExperimentSessionId());
        try {
            SimulationState current = sessionService.getCurrentState(sessionId);
            return sessionService.appendEvent(sessionId, current.version().value(), new IdempotencyKey("workspace-" + clientEventId),
                    new MaterialTransferredPayload(
                            string(source.get("id")),
                            string(target.get("id")),
                            materialId,
                            BigDecimal.valueOf(amountMl),
                            "mL",
                            phase,
                            BigDecimal.valueOf(targetCapacityMl)));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex);
        }
    }

    private void checkExpected(Long expectedVersion, WorkspaceEntity entity) {
        if (expectedVersion != null && expectedVersion != entity.getStateVersion()) {
            throw new VersionConflictException(expectedVersion, entity.getStateVersion());
        }
    }

    private boolean isReversible(String eventType) {
        return !Set.of("UNDO", "REDO").contains(eventType.toUpperCase(Locale.ROOT));
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
            applyEventToState(replay, null, event.getEventType(), payload, event.getClientEventId(), false);
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

    private void appendHistoryEvent(String workspaceId, String ownerId, String clientEventId, String eventType, long version, Map<String, Object> payload) {
        String eventId = "evt_" + UUID.randomUUID().toString().substring(0, 12);
        eventRepository.save(new WorkspaceEventEntity(eventId, workspaceId, ownerId, clientEventId, eventType, version, toJson(payload)));
    }

    private String require(Map<String, Object> payload, String field) {
        String value = string(payload.get(field));
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    private void validateConnection(List<Map<String, Object>> items, Map<String, Object> payload) {
        itemById(items, string(payload.getOrDefault("sourceItemId", payload.get("fromItemId"))));
        itemById(items, string(payload.getOrDefault("targetItemId", payload.get("toItemId"))));
        scienceAuthority.requireKnownPort(require(payload, payload.containsKey("sourcePort") ? "sourcePort" : "fromPort"));
        scienceAuthority.requireKnownPort(require(payload, payload.containsKey("targetPort") ? "targetPort" : "toPort"));
    }

    private Map<String, Object> itemById(List<Map<String, Object>> items, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("item id is required");
        }
        return items.stream()
                .filter(item -> itemId.equals(string(item.get("id"))))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown workspace item: " + itemId));
    }

    private double capacityMl(Map<String, Object> item) {
        Object raw = item.getOrDefault("capacityMl", item.getOrDefault("capacity", 1000.0));
        return positiveDouble(raw, "capacityMl");
    }

    private double volumeMl(Map<String, Object> item) {
        Object raw = item.getOrDefault("volumeMl", item.getOrDefault("volume", 0.0));
        if (raw instanceof Number n) return n.doubleValue();
        if (raw == null || raw.toString().isBlank()) return 0.0;
        return Double.parseDouble(raw.toString());
    }

    private double positiveDouble(Object raw, String field) {
        double value = raw instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(raw));
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    private double liquidLevel(double volume, double capacity) {
        return capacity <= 0 ? 0 : Math.max(0, Math.min(1, volume / capacity));
    }

    private boolean isEquipmentOperation(String eventType) {
        return Set.of("HEAT_START", "HEAT_STOP", "COOL", "FREEZE", "BOIL", "STIR_START", "STIR_STOP", "MIX", "WASH", "DRY")
                .contains(eventType.toUpperCase(Locale.ROOT));
    }

    private String normalizedPhase(String materialId, Object raw) {
        String value = string(raw);
        if (value.isBlank()) {
            throw new IllegalArgumentException("phase is required");
        }
        String normalized = value.toUpperCase(Locale.ROOT);
        if ("COMP-H2O".equalsIgnoreCase(materialId) && "LIQUID".equals(normalized)) {
            return "AQUEOUS";
        }
        return normalized;
    }

    private String string(Object value) {
        return value == null ? "" : value.toString();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    private <T> T fromJson(String json, TypeReference<T> ref) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, ref);
        } catch (Exception e) {
            return null;
        }
    }
}

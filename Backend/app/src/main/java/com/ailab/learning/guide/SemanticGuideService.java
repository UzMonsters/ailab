package com.ailab.learning.guide;

import com.ailab.learning.dto.LearningDtos.GuidePayload;
import com.ailab.learning.dto.LearningDtos.GuideTargetDto;
import com.ailab.learning.dto.LearningDtos.StepDefinitionDto;
import com.ailab.learning.dto.LearningDtos.TargetDescriptor;
import com.ailab.workspace.domain.WorkspaceStateEntity;
import com.ailab.workspace.repository.WorkspaceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SemanticGuideService {

    private final WorkspaceStateRepository workspaceStateRepository;
    private final ObjectMapper objectMapper;

    public SemanticGuideService(
            WorkspaceStateRepository workspaceStateRepository,
            ObjectMapper objectMapper
    ) {
        this.workspaceStateRepository = workspaceStateRepository;
        this.objectMapper = objectMapper;
    }

    public GuidePayload resolveGuide(
            StepDefinitionDto step,
            int hintLevel,
            String mode,
            String locale,
            String workspaceId
    ) {
        if (step == null) {
            return new GuidePayload(new TargetDescriptor("TAB", null, null, "equipment", null, null, null), "Select equipment tab", "top", 1);
        }

        int targetLevel = Math.max(1, Math.min(hintLevel, 3));
        if ("demo".equalsIgnoreCase(mode)) {
            targetLevel = 3;
        } else if ("detail".equalsIgnoreCase(mode)) {
            targetLevel = Math.max(2, targetLevel);
        }

        List<GuideTargetDto> targets = step.guideTargets();
        GuideTargetDto matchedTarget = null;

        if (targets != null && !targets.isEmpty()) {
            for (GuideTargetDto gt : targets) {
                if (gt.level() != null && gt.level() == targetLevel) {
                    matchedTarget = gt;
                    break;
                }
            }
            if (matchedTarget == null) {
                for (int lvl = targetLevel; lvl >= 1; lvl--) {
                    for (GuideTargetDto gt : targets) {
                        if (gt.level() != null && gt.level() == lvl) {
                            matchedTarget = gt;
                            break;
                        }
                    }
                    if (matchedTarget != null) break;
                }
            }
            if (matchedTarget == null) {
                matchedTarget = targets.get(0);
            }
        }

        String localizedText = resolveLocalizedInstruction(step, locale, targetLevel);
        String placement = matchedTarget != null && matchedTarget.placement() != null ? matchedTarget.placement() : "top";
        int sequence = matchedTarget != null && matchedTarget.sequence() != null ? matchedTarget.sequence() : targetLevel;

        TargetDescriptor descriptor = buildTargetDescriptor(matchedTarget, step, workspaceId, targetLevel);
        return new GuidePayload(descriptor, localizedText, placement, sequence);
    }

    private TargetDescriptor buildTargetDescriptor(
            GuideTargetDto target,
            StepDefinitionDto step,
            String workspaceId,
            int targetLevel
    ) {
        if (target == null) {
            return new TargetDescriptor("TAB", null, null, "equipment", null, null, null);
        }

        String kind = target.kind() != null ? target.kind() : "TAB";
        String catalogCode = target.catalogCode() != null ? target.catalogCode() : target.id();
        String itemId = target.itemId();
        String portId = target.portId();
        String sourcePortType = target.sourcePortType();
        String targetPortType = target.targetPortType();

        if (workspaceId != null && (itemId == null || portId == null)) {
            Optional<WorkspaceStateEntity> stateOpt = workspaceStateRepository.findById(workspaceId);
            if (stateOpt.isPresent()) {
                List<Map<String, Object>> items = parseJsonList(stateOpt.get().getItemsJson());
                for (Map<String, Object> item : items) {
                    String code = extractString(item, "catalogCode", "code", "type");
                    if (catalogCode != null && code != null && code.toLowerCase().contains(catalogCode.toLowerCase())) {
                        if (itemId == null) {
                            itemId = extractString(item, "id", "itemId");
                        }
                        if (portId == null && sourcePortType != null) {
                            portId = "sensor-out";
                        }
                    }
                }
            }
        }

        if (itemId == null && "ITEM".equalsIgnoreCase(kind)) {
            itemId = catalogCode != null ? catalogCode + "-1" : null;
        }

        return new TargetDescriptor(kind, itemId, portId, catalogCode, sourcePortType, targetPortType, null);
    }

    @SuppressWarnings("unchecked")
    private String resolveLocalizedInstruction(StepDefinitionDto step, String locale, int targetLevel) {
        String loc = locale != null && !locale.isBlank() ? locale.toLowerCase() : "ru";
        Map<String, Object> translations = step.translations();
        if (translations != null) {
            Object locObj = translations.get(loc);
            if (locObj == null) {
                locObj = translations.get("ru");
            }
            if (locObj == null) {
                locObj = translations.get("en");
            }
            if (locObj instanceof Map<?, ?> map) {
                Object instr = map.get("instruction");
                if (instr != null) return instr.toString();
                Object title = map.get("title");
                if (title != null) return title.toString();
            }
        }
        return "Complete current step";
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
}

package com.ailab.workspace.dto;

import java.util.List;
import java.util.Map;

public record WorkspaceItemDto(
        String id,
        String type,
        String profileId,
        String name,
        Map<String, Object> position,
        Map<String, Object> rotation,
        Double capacityMl,
        Double volumeMl,
        Double liquidLevel,
        Double temperatureC,
        String phase,
        List<ItemContentPortionDto> contents,
        ItemAppearanceDto appearance,
        String operation,
        Map<String, Object> properties
) {
    public WorkspaceItemDto {
        contents = contents == null ? List.of() : List.copyOf(contents);
        properties = properties == null ? Map.of() : Map.copyOf(properties);
    }
}

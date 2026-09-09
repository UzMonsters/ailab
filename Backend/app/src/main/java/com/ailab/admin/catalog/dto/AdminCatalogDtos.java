package com.ailab.admin.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AdminCatalogDtos {

    public record PageInfo(
            int page,
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {
        public PageInfo(int page, int size, long totalElements, int totalPages) {
            this(page, size, totalElements, totalPages, page);
        }
    }

    public record CatalogListResponse<T>(
            List<T> items,
            PageInfo page
    ) {}

    public record CatalogItemRow(
            String id,
            String code,
            String status,
            Long version,
            Map<String, Object> translations,
            Instant updatedAt
    ) {}

    public record CreateMaterialRequest(
            String code,
            String internalName,
            String formula,
            String phase,
            String hazardClass,
            Map<String, Object> appearance,
            Map<String, Object> physicalProperties,
            Map<String, Object> safety,
            Map<String, Object> translations
    ) {}

    public record PatchMaterialRequest(
            String code,
            String internalName,
            String formula,
            String phase,
            String hazardClass,
            Map<String, Object> appearance,
            Map<String, Object> physicalProperties,
            Map<String, Object> safety,
            Map<String, Object> translations,
            Long expectedVersion,
            Long version
    ) {}

    public record MaterialDocument(
            String id,
            String code,
            String internalName,
            String formula,
            String phase,
            String hazardClass,
            Map<String, Object> appearance,
            Map<String, Object> physicalProperties,
            Map<String, Object> safety,
            Map<String, Object> translations,
            String status,
            Long version,
            Long publishedVersion,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record PortSpec(
            String id,
            String kind,
            String type,
            String direction,
            Object capacity,
            String connector
    ) {}

    public record UpdatePortsRequest(
            Long expectedVersion,
            List<Map<String, Object>> ports
    ) {}

    public record CompatibilityRule(
            String materialCode,
            Boolean allowed
    ) {}

    public record UpdateCompatibilityRequest(
            Long expectedVersion,
            List<Map<String, Object>> rules
    ) {}

    public record CreateEquipmentRequest(
            String code,
            String name,
            String category,
            String kind,
            String rendererKey,
            List<String> capabilities,
            Map<String, Object> limits,
            List<Map<String, Object>> ports,
            Map<String, Object> translations
    ) {}

    public record PatchEquipmentRequest(
            String code,
            String name,
            String category,
            String kind,
            String rendererKey,
            List<String> capabilities,
            Map<String, Object> limits,
            List<Map<String, Object>> ports,
            Map<String, Object> translations,
            Long expectedVersion,
            Long version
    ) {}

    public record EquipmentDocument(
            String id,
            String code,
            String name,
            String category,
            String kind,
            String rendererKey,
            List<String> capabilities,
            Map<String, Object> limits,
            List<Map<String, Object>> ports,
            List<Map<String, Object>> compatibilityRules,
            Map<String, Object> translations,
            String status,
            Long version,
            Long publishedVersion,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record CreateScenarioRequest(
            String code,
            String subject,
            String trackId,
            String difficulty,
            Integer order,
            Map<String, Object> initialScene,
            Map<String, Object> scenario,
            List<Map<String, Object>> steps,
            List<Map<String, Object>> checkpoints,
            List<String> guideTargets,
            Map<String, Object> translations
    ) {}

    public record PatchScenarioRequest(
            String code,
            String subject,
            String trackId,
            String difficulty,
            Integer order,
            Map<String, Object> initialScene,
            Map<String, Object> scenario,
            List<Map<String, Object>> steps,
            List<Map<String, Object>> checkpoints,
            List<String> guideTargets,
            Map<String, Object> translations,
            Long expectedVersion,
            Long version
    ) {}

    public record ScenarioDocument(
            String id,
            String code,
            String status,
            Map<String, Object> initialScene,
            Map<String, Object> scenario,
            List<Map<String, Object>> steps,
            List<Map<String, Object>> checkpoints,
            List<String> guideTargets,
            Map<String, Object> translations,
            Long version,
            Long publishedVersion,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record CreateElementRequest(
            Integer atomicNumber,
            String symbol,
            String name,
            Map<String, Object> properties,
            Map<String, Object> translations
    ) {}

    public record CreateSubstanceRequest(
            String code,
            String formula,
            String phase,
            Map<String, Object> appearance,
            Map<String, Object> properties,
            List<String> hazards,
            Map<String, Object> translations
    ) {}

    public record CreateReactionRequest(
            String code,
            List<Map<String, Object>> reactants,
            List<Map<String, Object>> products,
            Map<String, Object> conditions,
            Map<String, Object> energy,
            Map<String, Object> appearance,
            Map<String, Object> safety,
            Map<String, Object> translations
    ) {}

    public record ValidationReportDto(
            boolean valid,
            List<String> errors,
            List<String> warnings
    ) {}

    public record PublishResultDto(
            Long publishedVersion,
            Instant publishedAt
    ) {}
}

package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessRepository;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStatus;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStep;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessVersion;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessContainerRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessEnvironmentRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessEquipmentRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessMaterialRequirement;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepDependency;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepId;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessStepType;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("local | prod | migration-test")
public class JdbcLaboratoryProcessRepositoryAdapter implements LaboratoryProcessRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public JdbcLaboratoryProcessRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public LaboratoryProcessDefinition save(LaboratoryProcessDefinition definition) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.laboratory_process_definitions (process_code, process_version, status)
                VALUES (?, ?, ?)
                ON CONFLICT (process_code, process_version)
                DO UPDATE SET status = EXCLUDED.status, updated_at = NOW()
                """, definition.code(), definition.version().value(), definition.status().name());

        jdbcTemplate.update("DELETE FROM chemistry.laboratory_process_requirements WHERE process_code = ? AND process_version = ?",
                definition.code(), definition.version().value());
        jdbcTemplate.update("DELETE FROM chemistry.laboratory_process_ports WHERE process_code = ? AND process_version = ?",
                definition.code(), definition.version().value());
        jdbcTemplate.update("DELETE FROM chemistry.laboratory_process_step_dependencies WHERE process_code = ? AND process_version = ?",
                definition.code(), definition.version().value());
        jdbcTemplate.update("DELETE FROM chemistry.laboratory_process_steps WHERE process_code = ? AND process_version = ?",
                definition.code(), definition.version().value());

        int index = 0;
        for (LaboratoryProcessStep step : definition.steps()) {
            jdbcTemplate.update("""
                    INSERT INTO chemistry.laboratory_process_steps
                    (process_code, process_version, step_id, step_type, optional, expected_duration_seconds, step_order)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, definition.code(), definition.version().value(), step.id().value(), step.type().name(),
                    step.optional(), step.expectedDuration().in(DurationUnit.SECOND), index++);
            for (ProcessStepDependency dependency : step.dependencies()) {
                jdbcTemplate.update("""
                        INSERT INTO chemistry.laboratory_process_step_dependencies
                        (process_code, process_version, step_id, depends_on_step_id)
                        VALUES (?, ?, ?, ?)
                        """, definition.code(), definition.version().value(), step.id().value(), dependency.stepId().value());
            }
            saveRequirements(definition, step);
            for (String input : step.inputPortIds()) {
                savePort(definition, step, input, "INPUT");
            }
            for (String output : step.outputPortIds()) {
                savePort(definition, step, output, "OUTPUT");
            }
        }
        return definition;
    }

    @Override
    public Optional<LaboratoryProcessDefinition> findByCodeAndVersion(String code, int version) {
        List<Map<String, Object>> definitions = jdbcTemplate.queryForList("""
                SELECT process_code, process_version, status
                FROM chemistry.laboratory_process_definitions
                WHERE process_code = ? AND process_version = ?
                """, code, version);
        if (definitions.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Object> row = definitions.getFirst();
        return Optional.of(new LaboratoryProcessDefinition(
                (String) row.get("process_code"),
                new LaboratoryProcessVersion(((Number) row.get("process_version")).intValue()),
                LaboratoryProcessStatus.valueOf((String) row.get("status")),
                steps(code, version)));
    }

    private List<LaboratoryProcessStep> steps(String code, int version) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT step_id, step_type, optional, expected_duration_seconds
                FROM chemistry.laboratory_process_steps
                WHERE process_code = ? AND process_version = ?
                ORDER BY step_order
                """, code, version);
        List<LaboratoryProcessStep> steps = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String stepId = (String) row.get("step_id");
            steps.add(new LaboratoryProcessStep(
                    new ProcessStepId(stepId),
                    ProcessStepType.valueOf((String) row.get("step_type")),
                    (Boolean) row.get("optional"),
                    Duration.of((BigDecimal) row.get("expected_duration_seconds"), DurationUnit.SECOND),
                    dependencies(code, version, stepId),
                    materialRequirements(code, version, stepId),
                    equipmentRequirements(code, version, stepId),
                    containerRequirements(code, version, stepId),
                    environmentRequirements(code, version, stepId),
                    ports(code, version, stepId, "INPUT"),
                    ports(code, version, stepId, "OUTPUT"),
                    scientificOperationSpecifications(code, version, stepId)));
        }
        return steps;
    }

    private void saveRequirements(LaboratoryProcessDefinition definition, LaboratoryProcessStep step) {
        for (ProcessMaterialRequirement requirement : step.materialRequirements()) {
            saveRequirement(definition, step, requirement.requirementId(), "MATERIAL", requirement);
        }
        for (ProcessEquipmentRequirement requirement : step.equipmentRequirements()) {
            saveRequirement(definition, step, requirement.requirementId(), "EQUIPMENT", requirement);
        }
        for (ProcessContainerRequirement requirement : step.containerRequirements()) {
            saveRequirement(definition, step, requirement.requirementId(), "CONTAINER", requirement);
        }
        for (ProcessEnvironmentRequirement requirement : step.environmentRequirements()) {
            saveRequirement(definition, step, requirement.requirementId(), "ENVIRONMENT", requirement);
        }
        int index = 0;
        for (ScientificOperationSpecification specification : step.scientificOperationSpecifications()) {
            saveRequirement(definition, step, "scientific-operation-" + index++, "SCIENTIFIC_OPERATION", specification);
        }
    }

    private void saveRequirement(LaboratoryProcessDefinition definition, LaboratoryProcessStep step,
                                 String requirementId, String type, Object payload) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.laboratory_process_requirements
                (process_code, process_version, step_id, requirement_id, requirement_type, requirement_payload)
                VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb))
                """, definition.code(), definition.version().value(), step.id().value(), requirementId, type, write(payload));
    }

    private void savePort(LaboratoryProcessDefinition definition, LaboratoryProcessStep step, String portId, String direction) {
        jdbcTemplate.update("""
                INSERT INTO chemistry.laboratory_process_ports
                (process_code, process_version, step_id, port_id, port_direction)
                VALUES (?, ?, ?, ?, ?)
                """, definition.code(), definition.version().value(), step.id().value(), portId, direction);
    }

    private List<ProcessStepDependency> dependencies(String code, int version, String stepId) {
        return jdbcTemplate.query("""
                SELECT depends_on_step_id
                FROM chemistry.laboratory_process_step_dependencies
                WHERE process_code = ? AND process_version = ? AND step_id = ?
                ORDER BY depends_on_step_id
                """, (rs, rowNum) -> new ProcessStepDependency(new ProcessStepId(rs.getString("depends_on_step_id"))),
                code, version, stepId);
    }

    private List<String> ports(String code, int version, String stepId, String direction) {
        return jdbcTemplate.queryForList("""
                SELECT port_id
                FROM chemistry.laboratory_process_ports
                WHERE process_code = ? AND process_version = ? AND step_id = ? AND port_direction = ?
                ORDER BY port_id
                """, String.class, code, version, stepId, direction);
    }

    private List<ProcessMaterialRequirement> materialRequirements(String code, int version, String stepId) {
        return requirements(code, version, stepId, "MATERIAL", ProcessMaterialRequirement.class);
    }

    private List<ProcessEquipmentRequirement> equipmentRequirements(String code, int version, String stepId) {
        return requirements(code, version, stepId, "EQUIPMENT", ProcessEquipmentRequirement.class);
    }

    private List<ProcessContainerRequirement> containerRequirements(String code, int version, String stepId) {
        return requirements(code, version, stepId, "CONTAINER", ProcessContainerRequirement.class);
    }

    private List<ProcessEnvironmentRequirement> environmentRequirements(String code, int version, String stepId) {
        return requirements(code, version, stepId, "ENVIRONMENT", ProcessEnvironmentRequirement.class);
    }

    private List<ScientificOperationSpecification> scientificOperationSpecifications(String code, int version, String stepId) {
        return requirements(code, version, stepId, "SCIENTIFIC_OPERATION", ScientificOperationSpecification.class);
    }

    private <T> List<T> requirements(String code, int version, String stepId, String type, Class<T> clazz) {
        List<String> rows = jdbcTemplate.queryForList("""
                SELECT requirement_payload::text
                FROM chemistry.laboratory_process_requirements
                WHERE process_code = ? AND process_version = ? AND step_id = ? AND requirement_type = ?
                ORDER BY requirement_id
                """, String.class, code, version, stepId, type);
        List<T> result = new ArrayList<>();
        for (String row : rows) {
            try {
                result.add(mapper.readValue(row, clazz));
            } catch (JsonProcessingException ex) {
                throw new IllegalStateException("Could not read process requirement JSON", ex);
            }
        }
        return result;
    }

    private String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not write process requirement JSON", ex);
        }
    }
}

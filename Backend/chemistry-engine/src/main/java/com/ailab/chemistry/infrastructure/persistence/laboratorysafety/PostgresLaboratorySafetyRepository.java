package com.ailab.chemistry.infrastructure.persistence.laboratorysafety;

import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRule;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleId;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleType;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleVersion;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetySeverity;
import com.ailab.chemistry.domain.laboratorysafety.SafetyEvaluationStage;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleApplicability;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleCondition;
import com.ailab.chemistry.domain.laboratorysafety.SafetyRuleProvenance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Profile("local | prod | migration-test")
public class PostgresLaboratorySafetyRepository implements LaboratorySafetyRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostgresLaboratorySafetyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LaboratorySafetyRule> findAllActiveRules() {
        String sql = "SELECT * FROM chemistry.laboratory_safety_rules WHERE active = true";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                String ruleId = rs.getString("rule_id");
                int version = rs.getInt("rule_version");
                LaboratorySafetyRuleType ruleType = LaboratorySafetyRuleType.valueOf(rs.getString("rule_type"));
                LaboratorySafetySeverity severity = LaboratorySafetySeverity.valueOf(rs.getString("severity"));
                SafetyEvaluationStage stage = SafetyEvaluationStage.valueOf(rs.getString("evaluation_stage"));

                Set<String> opTypes = objectMapper.readValue(rs.getString("operation_types"),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                Set<String> reqFields = objectMapper.readValue(rs.getString("required_input_fields"),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));

                String condField = rs.getString("condition_field");
                String condOp = rs.getString("condition_operator");
                String condTarget = rs.getString("condition_target_value");

                String srcTypeStr = rs.getString("source_type");
                com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType srcType = srcTypeStr != null
                        ? com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType.valueOf(srcTypeStr)
                        : com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType.INTERNAL_GOVERNED_POLICY;
                String srcId = rs.getString("source_identifier");
                String srcCitation = rs.getString("source_citation");
                String srcDate = rs.getString("source_version_date");
                String clauseSec = rs.getString("exact_clause_section");
                String suppClaim = rs.getString("supported_claim");
                String srcUrl = rs.getString("source_url");
                String evStatus = rs.getString("evidence_status");
                int effVer = rs.getInt("effective_version");

                return new LaboratorySafetyRule(
                        new LaboratorySafetyRuleId(ruleId),
                        new LaboratorySafetyRuleVersion(version),
                        ruleType,
                        severity,
                        new SafetyRuleApplicability(stage, opTypes, reqFields),
                        new SafetyRuleCondition(condField, condOp, condTarget, java.util.Map.of()),
                        new SafetyRuleProvenance(srcType, srcId, srcCitation, srcDate, clauseSec, suppClaim, srcUrl, evStatus, effVer),
                        true
                );
            } catch (Exception e) {
                throw new RuntimeException("Error mapping LaboratorySafetyRule", e);
            }
        });
    }

    @Override
    public Optional<LaboratorySafetyRule> findByRuleIdAndVersion(LaboratorySafetyRuleId ruleId, LaboratorySafetyRuleVersion version) {
        String sql = "SELECT * FROM chemistry.laboratory_safety_rules WHERE rule_id = ? AND rule_version = ?";
        List<LaboratorySafetyRule> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                LaboratorySafetyRuleType ruleType = LaboratorySafetyRuleType.valueOf(rs.getString("rule_type"));
                LaboratorySafetySeverity severity = LaboratorySafetySeverity.valueOf(rs.getString("severity"));
                SafetyEvaluationStage stage = SafetyEvaluationStage.valueOf(rs.getString("evaluation_stage"));

                Set<String> opTypes = objectMapper.readValue(rs.getString("operation_types"),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                Set<String> reqFields = objectMapper.readValue(rs.getString("required_input_fields"),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));

                String condField = rs.getString("condition_field");
                String condOp = rs.getString("condition_operator");
                String condTarget = rs.getString("condition_target_value");

                String srcTypeStr = rs.getString("source_type");
                com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType srcType = srcTypeStr != null
                        ? com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType.valueOf(srcTypeStr)
                        : com.ailab.chemistry.domain.laboratorysafety.SafetyRuleSourceType.INTERNAL_GOVERNED_POLICY;
                String srcId = rs.getString("source_identifier");
                String srcCitation = rs.getString("source_citation");
                String srcDate = rs.getString("source_version_date");
                String clauseSec = rs.getString("exact_clause_section");
                String suppClaim = rs.getString("supported_claim");
                String srcUrl = rs.getString("source_url");
                String evStatus = rs.getString("evidence_status");
                int effVer = rs.getInt("effective_version");
                boolean active = rs.getBoolean("active");

                return new LaboratorySafetyRule(
                        ruleId,
                        version,
                        ruleType,
                        severity,
                        new SafetyRuleApplicability(stage, opTypes, reqFields),
                        new SafetyRuleCondition(condField, condOp, condTarget, java.util.Map.of()),
                        new SafetyRuleProvenance(srcType, srcId, srcCitation, srcDate, clauseSec, suppClaim, srcUrl, evStatus, effVer),
                        active
                );
            } catch (Exception e) {
                throw new RuntimeException("Error mapping LaboratorySafetyRule", e);
            }
        }, ruleId.value(), version.value());
        return list.stream().findFirst();
    }

    @Override
    public void saveAudit(LaboratorySafetyEvaluationResult result, String sessionId, String commandId, String eventId) {
        String sql = "INSERT INTO chemistry.simulation_safety_audits " +
                "(session_id, command_id, event_id, evaluation_stage, status, evaluated_rule_versions, violations, warnings) " +
                "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb)";
        try {
            jdbcTemplate.update(sql,
                    sessionId,
                    commandId,
                    eventId,
                    result.stage().name(),
                    result.status().name(),
                    objectMapper.writeValueAsString(result.evaluatedRuleVersions()),
                    objectMapper.writeValueAsString(result.violations()),
                    objectMapper.writeValueAsString(result.warnings())
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving safety audit", e);
        }
    }
}

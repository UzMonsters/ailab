package com.ailab.chemistry.domain.laboratorysafety;

import java.util.List;
import java.util.Optional;

public interface LaboratorySafetyRepository {
    List<LaboratorySafetyRule> findAllActiveRules();
    Optional<LaboratorySafetyRule> findByRuleIdAndVersion(LaboratorySafetyRuleId ruleId, LaboratorySafetyRuleVersion version);
    void saveAudit(LaboratorySafetyEvaluationResult result, String sessionId, String commandId, String eventId);
}

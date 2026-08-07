package com.ailab.chemistry.infrastructure.persistence.laboratorysafety;

import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRule;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleId;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleVersion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!local & !prod & !migration-test")
public class UnavailableLaboratorySafetyRepository implements LaboratorySafetyRepository {

    @Override
    public List<LaboratorySafetyRule> findAllActiveRules() {
        return List.of();
    }

    @Override
    public Optional<LaboratorySafetyRule> findByRuleIdAndVersion(LaboratorySafetyRuleId ruleId, LaboratorySafetyRuleVersion version) {
        return Optional.empty();
    }

    @Override
    public void saveAudit(LaboratorySafetyEvaluationResult result, String sessionId, String commandId, String eventId) {
    }
}

package com.ailab.chemistry;

import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRule;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleId;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRuleVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class TestChemistryApplication {

    @Bean
    @org.springframework.context.annotation.Primary
    public LaboratorySafetyRepository laboratorySafetyRepository() {
        return new LaboratorySafetyRepository() {
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
        };
    }
}

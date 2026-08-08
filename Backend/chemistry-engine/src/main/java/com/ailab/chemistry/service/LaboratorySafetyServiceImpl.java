package com.ailab.chemistry.service;

import com.ailab.chemistry.api.LaboratorySafetyService;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyCalculator;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRepository;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratorySafetyServiceImpl implements LaboratorySafetyService {

    private final LaboratorySafetyRepository safetyRepository;
    private final LaboratorySafetyCalculator calculator = new LaboratorySafetyCalculator();

    public LaboratorySafetyServiceImpl(LaboratorySafetyRepository safetyRepository) {
        this.safetyRepository = safetyRepository;
    }

    @Override
    public LaboratorySafetyEvaluationResult evaluate(LaboratorySafetyEvaluationRequest request) {
        List<LaboratorySafetyRule> activeRules = safetyRepository.findAllActiveRules();
        return calculator.evaluate(activeRules, request);
    }
}

package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;

public interface LaboratorySafetyService {
    LaboratorySafetyEvaluationResult evaluate(LaboratorySafetyEvaluationRequest request);
}

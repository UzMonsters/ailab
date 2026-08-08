package com.ailab.chemistry.controller;

import com.ailab.chemistry.api.LaboratorySafetyService;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationRequest;
import com.ailab.chemistry.domain.laboratorysafety.LaboratorySafetyEvaluationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chemistry/safety")
@Tag(name = "Laboratory Safety & Governance", description = "Pre-run laboratory safety rule evaluation, operating limit checks, and hazard assessment")
@SecurityRequirement(name = "bearerAuth")
public class LaboratorySafetyController {

    private final LaboratorySafetyService safetyService;

    public LaboratorySafetyController(LaboratorySafetyService safetyService) {
        this.safetyService = safetyService;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate laboratory safety rules", description = "Pre-evaluate proposed laboratory experiment parameters against governed safety rules (SAFE-FUME-HOOD-REQ, thermal limits, vessel pressure, acid-water order) to check for prohibitions or warnings.")
    public LaboratorySafetyEvaluationResult evaluateSafety(@Valid @RequestBody LaboratorySafetyEvaluationRequest request) {
        return safetyService.evaluate(request);
    }
}

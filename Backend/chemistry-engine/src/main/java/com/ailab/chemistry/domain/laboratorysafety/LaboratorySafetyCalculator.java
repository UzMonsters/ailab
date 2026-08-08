package com.ailab.chemistry.domain.laboratorysafety;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LaboratorySafetyCalculator {

    public LaboratorySafetyEvaluationResult evaluate(List<LaboratorySafetyRule> rules, LaboratorySafetyEvaluationRequest request) {
        List<LaboratorySafetyViolation> violations = new ArrayList<>();
        List<LaboratorySafetyWarning> warnings = new ArrayList<>();
        Set<String> evaluatedRuleVersions = new HashSet<>();
        boolean missingRequiredData = false;

        String opType = request.command().operation().operationType().name();

        for (LaboratorySafetyRule rule : rules) {
            if (!rule.active()) {
                continue;
            }
            SafetyRuleApplicability app = rule.applicability();
            if (app.stage() != request.stage()) {
                continue;
            }
            if (!app.operationTypes().isEmpty() && !app.operationTypes().contains(opType)) {
                continue;
            }

            evaluatedRuleVersions.add(rule.ruleId().value() + ":" + rule.version().value());

            for (String field : app.requiredInputFields()) {
                if (!hasValue(request, field)) {
                    missingRequiredData = true;
                    violations.add(new LaboratorySafetyViolation(
                            rule.ruleId(),
                            rule.version(),
                            rule.ruleType(),
                            "Required safety evaluation field missing: " + field,
                            rule.provenance().sourceCitation()));
                    break;
                }
            }

            if (evaluatesCondition(rule.condition(), request)) {
                if (rule.severity() == LaboratorySafetySeverity.CRITICAL) {
                    violations.add(new LaboratorySafetyViolation(
                            rule.ruleId(),
                            rule.version(),
                            rule.ruleType(),
                            "Safety rule condition triggered: " + rule.ruleType(),
                            rule.provenance().sourceCitation()));
                } else {
                    warnings.add(new LaboratorySafetyWarning(
                            rule.ruleId(),
                            rule.version(),
                            rule.ruleType(),
                            "Safety warning condition triggered: " + rule.ruleType(),
                            rule.provenance().sourceCitation()));
                }
            }
        }

        LaboratorySafetyStatus status;
        if (!violations.isEmpty()) {
            status = missingRequiredData ? LaboratorySafetyStatus.INSUFFICIENT_DATA : LaboratorySafetyStatus.BLOCKED;
        } else if (!warnings.isEmpty()) {
            status = LaboratorySafetyStatus.ALLOWED_WITH_WARNINGS;
        } else {
            status = LaboratorySafetyStatus.ALLOWED;
        }

        return new LaboratorySafetyEvaluationResult(status, request.stage(), violations, warnings, evaluatedRuleVersions);
    }

    private boolean hasValue(LaboratorySafetyEvaluationRequest request, String field) {
        if (request.environmentContext().containsKey(field)) return true;
        if (request.command().inputs().containsKey(field)) return true;
        if (request.proposedDelta().isPresent()) {
            var delta = request.proposedDelta().get();
            for (var v : delta.vesselDeltas()) {
                if (field.equals("temperatureK") && v.finalTemperatureKelvin() != null) return true;
                if (field.equals("pressureKPa") && v.finalPressureKpa() != null) return true;
                if (field.equals("volumeMl") && v.finalVolumeMl() != null) return true;
            }
        }
        return false;
    }

    private boolean evaluatesCondition(SafetyRuleCondition condition, LaboratorySafetyEvaluationRequest request) {
        String actualStr = getValue(request, condition.field());
        if (actualStr == null) return false;

        String target = condition.targetValue();
        switch (condition.operator()) {
            case "EQUALS":
                return actualStr.equalsIgnoreCase(target);
            case "NOT_EQUALS":
                return !actualStr.equalsIgnoreCase(target);
            case "GREATER_THAN":
                try {
                    BigDecimal act = new BigDecimal(actualStr);
                    BigDecimal tgt = new BigDecimal(target);
                    return act.compareTo(tgt) > 0;
                } catch (Exception e) {
                    return false;
                }
            default:
                return false;
        }
    }

    private String getValue(LaboratorySafetyEvaluationRequest request, String field) {
        if (request.environmentContext().containsKey(field)) {
            return request.environmentContext().get(field);
        }
        if (request.command().inputs().containsKey(field)) {
            return request.command().inputs().get(field);
        }
        if (request.proposedDelta().isPresent()) {
            var delta = request.proposedDelta().get();
            for (var v : delta.vesselDeltas()) {
                if (field.equals("temperatureK") && v.finalTemperatureKelvin() != null) return v.finalTemperatureKelvin().toPlainString();
                if (field.equals("pressureKPa") && v.finalPressureKpa() != null) return v.finalPressureKpa().toPlainString();
                if (field.equals("volumeMl") && v.finalVolumeMl() != null) return v.finalVolumeMl().toPlainString();
            }
        }
        return null;
    }
}

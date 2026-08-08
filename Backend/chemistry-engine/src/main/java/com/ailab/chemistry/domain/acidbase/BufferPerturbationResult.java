package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;

import java.util.Objects;
import java.util.Optional;

public final class BufferPerturbationResult {
    private final BufferRegionStatus status;
    private final BufferCalculationResult result;
    private final AmountOfSubstance remainingExcessAmount;
    private final AcidBaseEquilibriumResult delegatedEquilibriumResult;
    private final BufferCalculationMethod calculationMethod;
    private final String explanation;

    public BufferPerturbationResult(
            BufferRegionStatus status,
            BufferCalculationResult result,
            AmountOfSubstance remainingExcessAmount,
            AcidBaseEquilibriumResult delegatedEquilibriumResult,
            BufferCalculationMethod calculationMethod,
            String explanation) {
        this.status = Objects.requireNonNull(status);
        this.result = result;
        this.remainingExcessAmount = remainingExcessAmount;
        this.delegatedEquilibriumResult = delegatedEquilibriumResult;
        this.calculationMethod = Objects.requireNonNull(calculationMethod);
        this.explanation = explanation == null ? "" : explanation;
    }

    public static BufferPerturbationResult valid(BufferCalculationResult result) {
        return new BufferPerturbationResult(BufferRegionStatus.VALID_BUFFER, result, null, null,
                BufferCalculationMethod.STOICHIOMETRIC_NEUTRALIZATION_THEN_HENDERSON_HASSELBALCH,
                "Strong reagent was consumed stoichiometrically before Henderson-Hasselbalch recalculation.");
    }

    public static BufferPerturbationResult exact(BufferRegionStatus exhaustedStatus, String explanation) {
        return new BufferPerturbationResult(exhaustedStatus, null, null, null,
                BufferCalculationMethod.EXACT_EXHAUSTION_DELEGATED_TO_PHASE_7D, explanation);
    }

    public static BufferPerturbationResult excess(BufferRegionStatus status, AmountOfSubstance remainingExcessAmount) {
        return new BufferPerturbationResult(status, null, remainingExcessAmount, null,
                BufferCalculationMethod.UNSUPPORTED_MIXED_STRONG_WEAK_EQUILIBRIUM,
                "Excess strong reagent remains after buffer exhaustion; a future mixed-equilibrium solver is required.");
    }

    public BufferPerturbationResult withDelegatedEquilibriumResult(AcidBaseEquilibriumResult delegatedResult) {
        return new BufferPerturbationResult(status, result, remainingExcessAmount, delegatedResult, calculationMethod, explanation);
    }

    public BufferRegionStatus getStatus() { return status; }
    public Optional<BufferCalculationResult> getResult() { return Optional.ofNullable(result); }
    public Optional<AmountOfSubstance> getRemainingExcessAmount() { return Optional.ofNullable(remainingExcessAmount); }
    public Optional<AcidBaseEquilibriumResult> getDelegatedEquilibriumResult() { return Optional.ofNullable(delegatedEquilibriumResult); }
    public BufferCalculationMethod getCalculationMethod() { return calculationMethod; }
    public String getExplanation() { return explanation; }
}

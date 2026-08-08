package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BufferCalculator {
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TWO_POINT_303 = new BigDecimal("2.303");
    private static final String WATER = "COMP-H2O";

    public BufferCalculationResult calculate(BufferCalculationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        BufferSystem system = request.maybeSystem().orElseThrow(() ->
                new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Buffer system must be resolved before pure calculation"));
        validateSystem(system);
        return calculateResolved(request, BufferCalculationMethod.HENDERSON_HASSELBALCH);
    }

    public BufferPreparationResult calculatePreparation(BufferPreparationRequest request) {
        validateSystem(request.system());
        BigDecimal pKa = p(request.system().ka());
        BigDecimal pKb = p(request.system().kb());
        BigDecimal pKw = p(request.system().kw());
        BigDecimal ratio;
        if (request.system().systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE) {
            ratio = AcidBaseDecimalMath.tenPower(request.targetPh().subtract(pKa, MC));
        } else {
            BigDecimal targetPoh = pKw.subtract(request.targetPh(), MC);
            ratio = AcidBaseDecimalMath.tenPower(targetPoh.subtract(pKb, MC));
        }
        if (!isFinitePositive(ratio)) {
            throw new BufferException(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST, "Required component ratio is numerically unsafe");
        }
        BigDecimal total = request.totalBufferConcentration();
        BigDecimal acidConc;
        BigDecimal baseConc;
        if (request.system().systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE) {
            acidConc = total.divide(ONE.add(ratio, MC), MC);
            baseConc = total.multiply(ratio, MC).divide(ONE.add(ratio, MC), MC);
        } else {
            baseConc = total.divide(ONE.add(ratio, MC), MC);
            acidConc = total.multiply(ratio, MC).divide(ONE.add(ratio, MC), MC);
        }
        MolarConcentration acidM = MolarConcentration.of(acidConc, MolarConcentrationUnit.MOL_PER_LITER);
        MolarConcentration baseM = MolarConcentration.of(baseConc, MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal liters = request.finalVolume().in(VolumeUnit.LITER);
        AmountOfSubstance acidAmount = AmountOfSubstance.of(acidConc.multiply(liters, MC), AmountOfSubstanceUnit.MOLE);
        AmountOfSubstance baseAmount = AmountOfSubstance.of(baseConc.multiply(liters, MC), AmountOfSubstanceUnit.MOLE);
        return new BufferPreparationResult(
                request.system(),
                request.targetPh(),
                ratio,
                acidM,
                baseM,
                acidAmount,
                baseAmount,
                statusFor(request.system(), request.targetPh()),
                BufferCalculationMethod.TARGET_RATIO_HENDERSON_HASSELBALCH,
                assumptions(),
                constants(request.system())
        );
    }

    public BufferCalculationResult dilute(BufferCalculationRequest request, Volume finalVolume) {
        BufferCalculationRequest diluted = BufferCalculationRequest.fromAmounts(
                request.maybeSystem().orElseThrow(() -> new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Buffer system must be resolved before dilution")),
                request.acidComponent().amount(),
                request.baseComponent().amount(),
                finalVolume
        );
        return calculateResolved(diluted, BufferCalculationMethod.IDEAL_PROPORTIONAL_DILUTION);
    }

    public BufferPerturbationResult addStrongAcidOrBase(BufferPerturbationRequest request) {
        BufferCalculationRequest initial = request.initialBuffer();
        BufferSystem system = initial.maybeSystem().orElseThrow(() ->
                new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Buffer system must be resolved before perturbation"));
        BigDecimal acid = initial.acidComponent().moles();
        BigDecimal base = initial.baseComponent().moles();
        BigDecimal reagent = request.reagentAmount().in(AmountOfSubstanceUnit.MOLE);

        if (system.systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE) {
            if (request.reagentType() == StrongReagentType.STRONG_ACID) {
                return perturb(request, acid.add(reagent, MC), base.subtract(reagent, MC), BufferRegionStatus.EXCESS_STRONG_ACID_UNSUPPORTED, "Conjugate base exhausted; only weak acid remains.");
            }
            return perturb(request, acid.subtract(reagent, MC), base.add(reagent, MC), BufferRegionStatus.EXCESS_STRONG_BASE_UNSUPPORTED, "Weak acid exhausted; only conjugate base remains.");
        }

        if (request.reagentType() == StrongReagentType.STRONG_ACID) {
            return perturb(request, acid.add(reagent, MC), base.subtract(reagent, MC), BufferRegionStatus.EXCESS_STRONG_ACID_UNSUPPORTED, "Weak base exhausted; only conjugate acid remains.");
        }
        return perturb(request, acid.subtract(reagent, MC), base.add(reagent, MC), BufferRegionStatus.EXCESS_STRONG_BASE_UNSUPPORTED, "Conjugate acid exhausted; only weak base remains.");
    }

    private BufferPerturbationResult perturb(BufferPerturbationRequest request, BigDecimal newAcid, BigDecimal newBase, BufferRegionStatus excessStatus, String exactExplanation) {
        BufferCalculationRequest initial = request.initialBuffer();
        if (newAcid.compareTo(BigDecimal.ZERO) == 0 || newBase.compareTo(BigDecimal.ZERO) == 0) {
            return BufferPerturbationResult.exact(BufferRegionStatus.EXACT_EXHAUSTION, exactExplanation);
        }
        if (newAcid.compareTo(BigDecimal.ZERO) < 0 || newBase.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal excess = newAcid.compareTo(BigDecimal.ZERO) < 0 ? newAcid.abs(MC) : newBase.abs(MC);
            return BufferPerturbationResult.excess(excessStatus, AmountOfSubstance.of(excess, AmountOfSubstanceUnit.MOLE));
        }
        BufferCalculationRequest recalculated = BufferCalculationRequest.fromAmounts(
                initial.system(),
                AmountOfSubstance.of(newAcid, AmountOfSubstanceUnit.MOLE),
                AmountOfSubstance.of(newBase, AmountOfSubstanceUnit.MOLE),
                request.finalVolume()
        );
        return BufferPerturbationResult.valid(calculateResolved(recalculated, BufferCalculationMethod.STOICHIOMETRIC_NEUTRALIZATION_THEN_HENDERSON_HASSELBALCH));
    }

    private BufferCalculationResult calculateResolved(BufferCalculationRequest request, BufferCalculationMethod method) {
        BufferSystem system = request.system();
        BigDecimal acidMoles = request.acidComponent().moles();
        BigDecimal baseMoles = request.baseComponent().moles();
        BigDecimal ratio = baseMoles.divide(acidMoles, MC);
        BigDecimal pKa = p(system.ka());
        BigDecimal pKb = p(system.kb());
        BigDecimal pKw = p(system.kw());
        BigDecimal phValue;
        BigDecimal pohValue;

        if (system.systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE) {
            phValue = pKa.add(AcidBaseDecimalMath.log10(ratio), MC);
            pohValue = pKw.subtract(phValue, MC);
        } else {
            BigDecimal conjugateAcidToBase = acidMoles.divide(baseMoles, MC);
            pohValue = pKb.add(AcidBaseDecimalMath.log10(conjugateAcidToBase), MC);
            phValue = pKw.subtract(pohValue, MC);
        }

        PhValue ph = new PhValue(phValue.setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(pohValue.setScale(4, RoundingMode.HALF_UP));
        MolarConcentration total = totalConcentration(request);
        BufferCapacity capacity = capacity(system, phValue, total, ratio);
        BufferRegionStatus status = statusFor(system, phValue);
        return new BufferCalculationResult(
                system,
                request.acidComponent(),
                request.baseComponent(),
                ph,
                poh,
                ratio,
                total,
                capacity,
                status,
                method,
                assumptions(),
                constants(system),
                system.sources(),
                status == BufferRegionStatus.VALID_BUFFER ? "Henderson-Hasselbalch applicable while both buffer components remain positive." : "Result is mathematically valid but outside the recommended buffer range."
        );
    }

    private BufferCapacity capacity(BufferSystem system, BigDecimal ph, MolarConcentration total, BigDecimal ratio) {
        BigDecimal h = AcidBaseDecimalMath.tenPower(ph.negate(MC));
        BigDecimal ka = system.systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE
                ? system.ka()
                : system.kw().divide(system.kb(), MC);
        BigDecimal numerator = total.in(MolarConcentrationUnit.MOL_PER_LITER).multiply(ka, MC).multiply(h, MC);
        BigDecimal denominator = ka.add(h, MC).pow(2, MC);
        BigDecimal beta = TWO_POINT_303.multiply(h.add(system.kw().divide(h, MC), MC).add(numerator.divide(denominator, MC), MC), MC);
        return new BufferCapacity(beta, total, ratio, system.temperature(), BufferCalculationMethod.HENDERSON_HASSELBALCH,
                "Ideal monoprotic buffer capacity approximation; not activity-corrected thermodynamic capacity.");
    }

    private MolarConcentration totalConcentration(BufferCalculationRequest request) {
        BigDecimal totalMoles = request.acidComponent().moles().add(request.baseComponent().moles(), MC);
        BigDecimal liters = request.finalVolume().in(VolumeUnit.LITER);
        return MolarConcentration.of(totalMoles.divide(liters, MC), MolarConcentrationUnit.MOL_PER_LITER);
    }

    private BufferRegionStatus statusFor(BufferSystem system, BigDecimal ph) {
        BigDecimal anchor = system.systemType() == BufferSystemType.WEAK_ACID_CONJUGATE_BASE ? p(system.ka()) : p(system.kw()).subtract(p(system.kb()), MC);
        return ph.subtract(anchor, MC).abs(MC).compareTo(new BigDecimal("0.999")) >= 0
                ? BufferRegionStatus.OUTSIDE_RECOMMENDED_BUFFER_RANGE
                : BufferRegionStatus.VALID_BUFFER;
    }

    private Map<String, BigDecimal> constants(BufferSystem system) {
        return Map.of(
                "Ka", system.ka(),
                "Kb", system.kb(),
                "Kw", system.kw(),
                "pKa", p(system.ka()),
                "pKb", p(system.kb()),
                "pKw", p(system.kw())
        );
    }

    private List<BufferAssumption> assumptions() {
        return List.of(
                BufferAssumption.AQUEOUS_SOLVENT,
                BufferAssumption.IDEAL_SOLUTION,
                BufferAssumption.MONOPROTIC_CONJUGATE_PAIR,
                BufferAssumption.HENDERSON_HASSELBALCH_APPLICABILITY,
                BufferAssumption.COMPONENT_MOLE_RATIO_USED,
                BufferAssumption.TEMPERATURE_SPECIFIC_CONSTANTS,
                BufferAssumption.NO_ACTIVITY_COEFFICIENT_CORRECTION
        );
    }

    private void validateSystem(BufferSystem system) {
        if (!WATER.equalsIgnoreCase(system.solventCode())) {
            throw new BufferException(BufferErrorCode.UNSUPPORTED_SOLVENT, "Only aqueous buffers in COMP-H2O are supported");
        }
    }

    private BigDecimal p(BigDecimal k) {
        return AcidBaseDecimalMath.log10(k).negate(MC);
    }

    private boolean isFinitePositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }
}

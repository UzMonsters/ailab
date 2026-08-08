package com.ailab.chemistry.service;

import com.ailab.chemistry.api.AcidBaseEquilibriumService;
import com.ailab.chemistry.api.AcidBaseReferenceService;
import com.ailab.chemistry.api.BufferCalculationService;
import com.ailab.chemistry.api.ChemicalSpeciesDetails;
import com.ailab.chemistry.api.ConjugatePairDetails;
import com.ailab.chemistry.api.EquilibriumConstantDetails;
import com.ailab.chemistry.domain.acidbase.AcidBaseEquilibriumResult;
import com.ailab.chemistry.domain.acidbase.BufferCalculationRequest;
import com.ailab.chemistry.domain.acidbase.BufferCalculationResult;
import com.ailab.chemistry.domain.acidbase.BufferCalculator;
import com.ailab.chemistry.domain.acidbase.BufferErrorCode;
import com.ailab.chemistry.domain.acidbase.BufferException;
import com.ailab.chemistry.domain.acidbase.BufferPerturbationRequest;
import com.ailab.chemistry.domain.acidbase.BufferPerturbationResult;
import com.ailab.chemistry.domain.acidbase.BufferPreparationRequest;
import com.ailab.chemistry.domain.acidbase.BufferPreparationResult;
import com.ailab.chemistry.domain.acidbase.BufferRegionStatus;
import com.ailab.chemistry.domain.acidbase.BufferSystem;
import com.ailab.chemistry.domain.acidbase.BufferSystemType;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class BufferCalculationServiceImpl implements BufferCalculationService {

    private static final String WATER = "COMP-H2O";

    private final AcidBaseReferenceService referenceService;
    private final AcidBaseEquilibriumService equilibriumService;
    private final BufferCalculator calculator;

    @Autowired
    public BufferCalculationServiceImpl(AcidBaseReferenceService referenceService, AcidBaseEquilibriumService equilibriumService) {
        this.referenceService = Objects.requireNonNull(referenceService, "referenceService must not be null");
        this.equilibriumService = Objects.requireNonNull(equilibriumService, "equilibriumService must not be null");
        this.calculator = new BufferCalculator();
    }

    @Override
    public BufferCalculationResult calculateBuffer(BufferCalculationRequest request) {
        return calculator.calculate(withResolvedSystem(request));
    }

    @Override
    public BufferPreparationResult calculatePreparation(BufferPreparationRequest request) {
        return calculator.calculatePreparation(request);
    }

    @Override
    public BufferPerturbationResult addStrongAcidOrBase(BufferPerturbationRequest request) {
        BufferPerturbationRequest resolved = new BufferPerturbationRequest(
                withResolvedSystem(request.initialBuffer()),
                request.reagentType(),
                request.reagentAmount(),
                request.volumePolicy(),
                request.finalVolume()
        );
        BufferPerturbationResult result = calculator.addStrongAcidOrBase(resolved);
        if (result.getStatus() != BufferRegionStatus.EXACT_EXHAUSTION) {
            return result;
        }
        AcidBaseEquilibriumResult delegated = delegateExactExhaustion(resolved, result.getExplanation());
        return result.withDelegatedEquilibriumResult(delegated);
    }

    @Override
    public BufferCalculationResult calculateDilution(BufferCalculationRequest request, Volume finalVolume) {
        return calculator.dilute(withResolvedSystem(request), finalVolume);
    }

    @Override
    public BufferSystem resolveBufferSystem(String acidSpeciesCode, String baseSpeciesCode, Temperature temperature, String solventCode) {
        if (temperature == null) {
            throw new BufferException(BufferErrorCode.MISSING_TEMPERATURE, "Temperature is mandatory");
        }
        if (!WATER.equalsIgnoreCase(solventCode)) {
            throw new BufferException(BufferErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        ChemicalSpeciesDetails acid = referenceService.getSpecies(acidSpeciesCode);
        ChemicalSpeciesDetails base = referenceService.getSpecies(baseSpeciesCode);
        if ("AMPHIPROTIC".equalsIgnoreCase(acid.getPrimaryRole()) || "AMPHIPROTIC".equalsIgnoreCase(base.getPrimaryRole())) {
            throw new BufferException(BufferErrorCode.POLYPROTIC_BUFFER_UNSUPPORTED, "Polyprotic and amphiprotic buffer systems are deferred");
        }

        ConjugatePairDetails pair = referenceService.getConjugatePair(acidSpeciesCode);
        if (pair.getBaseSpeciesCode().equalsIgnoreCase(acidSpeciesCode) && pair.getAcidSpeciesCode().equalsIgnoreCase(baseSpeciesCode)) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR_ORIENTATION, "Conjugate pair is reversed");
        }
        if (!pair.getAcidSpeciesCode().equalsIgnoreCase(acidSpeciesCode) || !pair.getBaseSpeciesCode().equalsIgnoreCase(baseSpeciesCode)) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR, "Species are not a validated conjugate pair");
        }
        if (!"ACID".equalsIgnoreCase(acid.getPrimaryRole()) && !"AMPHIPROTIC".equalsIgnoreCase(acid.getPrimaryRole())) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR, "Acid component must have ACID or AMPHIPROTIC role");
        }
        if (!"BASE".equalsIgnoreCase(base.getPrimaryRole()) && !"AMPHIPROTIC".equalsIgnoreCase(base.getPrimaryRole())) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR, "Base component must have BASE or AMPHIPROTIC role");
        }

        EquilibriumConstantDetails ka = referenceService.findKa(acidSpeciesCode, temperature, solventCode)
                .orElseThrow(() -> new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Ka for acid component at requested temperature"));
        EquilibriumConstantDetails kb = referenceService.findKb(baseSpeciesCode, temperature, solventCode)
                .orElseThrow(() -> new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kb for base component at requested temperature"));
        EquilibriumConstantDetails kw = referenceService.findKa("SPEC-H2O", temperature, solventCode)
                .orElseThrow(() -> new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kw for requested temperature"));
        BufferSystemType systemType = "NEUTRAL_COMPOUND".equalsIgnoreCase(base.getKind())
                ? BufferSystemType.WEAK_BASE_CONJUGATE_ACID
                : BufferSystemType.WEAK_ACID_CONJUGATE_BASE;
        List<String> sources = new ArrayList<>();
        sources.add("CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)");
        return new BufferSystem(
                pair.getPairCode(),
                acidSpeciesCode,
                baseSpeciesCode,
                systemType,
                ka.getValue(),
                kb.getValue(),
                kw.getValue(),
                temperature,
                solventCode,
                sources
        );
    }

    private BufferCalculationRequest withResolvedSystem(BufferCalculationRequest request) {
        return request.maybeSystem().isPresent()
                ? request
                : request.withSystem(resolveBufferSystem(request.acidSpeciesCode(), request.baseSpeciesCode(), request.temperature(), request.solventCode()));
    }

    private AcidBaseEquilibriumResult delegateExactExhaustion(BufferPerturbationRequest request, String explanation) {
        BufferCalculationRequest initial = request.initialBuffer();
        BigDecimal totalBufferMoles = initial.acidComponent().moles().add(initial.baseComponent().moles(), java.math.MathContext.DECIMAL128);
        BigDecimal concentration = totalBufferMoles.divide(request.finalVolume().in(com.ailab.chemistry.domain.measurement.VolumeUnit.LITER), java.math.MathContext.DECIMAL128);
        MolarConcentration remaining = MolarConcentration.of(concentration, MolarConcentrationUnit.MOL_PER_LITER);
        if (explanation.contains("weak acid remains")) {
            return equilibriumService.calculateWeakAcid(initial.acidSpeciesCode(), remaining, initial.temperature());
        }
        if (explanation.contains("weak base remains")) {
            return equilibriumService.calculateWeakBase(initial.baseSpeciesCode(), remaining, initial.temperature());
        }
        if (explanation.contains("conjugate base remains")) {
            return equilibriumService.calculateSaltHydrolysis(initial.baseSpeciesCode(), remaining, initial.temperature());
        }
        return equilibriumService.calculateSaltHydrolysis(initial.acidSpeciesCode(), remaining, initial.temperature());
    }
}

package com.ailab.chemistry.service;

import com.ailab.chemistry.api.AcidBaseEquilibriumService;
import com.ailab.chemistry.api.AcidBaseReferenceService;
import com.ailab.chemistry.api.ChemicalSpeciesDetails;
import com.ailab.chemistry.api.EquilibriumConstantDetails;
import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.api.PolyproticEquilibriumService;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class IonicActivityServiceImpl implements IonicActivityService {
    private static final String WATER = "COMP-H2O";
    private static final String CRC_SOURCE = "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)";

    private final AcidBaseReferenceService referenceService;
    private final AcidBaseEquilibriumService equilibriumService;
    private final PolyproticEquilibriumService polyproticEquilibriumService;
    private final ActivityParameterSetRepository parameterSetRepository;
    private final IonicActivityCalculator calculator;

    @Autowired
    public IonicActivityServiceImpl(
            AcidBaseReferenceService referenceService,
            AcidBaseEquilibriumService equilibriumService,
            PolyproticEquilibriumService polyproticEquilibriumService,
            ActivityParameterSetRepository parameterSetRepository) {
        this.referenceService = Objects.requireNonNull(referenceService, "referenceService must not be null");
        this.equilibriumService = Objects.requireNonNull(equilibriumService, "equilibriumService must not be null");
        this.polyproticEquilibriumService = Objects.requireNonNull(polyproticEquilibriumService, "polyproticEquilibriumService must not be null");
        this.parameterSetRepository = Objects.requireNonNull(parameterSetRepository, "parameterSetRepository must not be null");
        this.calculator = new IonicActivityCalculator();
    }

    @Override
    public IonicStrength calculateIonicStrength(List<IonicSpeciesConcentration> species) {
        validateSpeciesCharges(species);
        return calculator.calculateIonicStrength(species);
    }

    @Override
    public ActivityCorrectionResult calculateActivities(
            List<IonicSpeciesConcentration> species,
            Temperature temperature,
            String solventCode,
            ActivityModel model) {
        validateSolvent(solventCode);
        validateSpeciesCharges(species);
        return calculator.calculateActivities(species, parameterSet(model, temperature, solventCode));
    }

    @Override
    public ActivityCorrectedEquilibriumResult calculateEquilibrium(ActivityCorrectionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        validateSolvent(request.solventCode());
        ActivityParameterSet parameterSet = parameterSet(request.model(), request.temperature(), request.solventCode());
        ActivityCorrectionRequest resolved = resolve(request, parameterSet);
        return calculator.calculateEquilibrium(resolved);
    }

    private ActivityCorrectionRequest resolve(ActivityCorrectionRequest request, ActivityParameterSet parameterSet) {
        BigDecimal kw = getKw(request.temperature(), request.solventCode());
        return switch (request.systemType()) {
            case PURE_WATER -> {
                equilibriumService.calculatePureWater(request.temperature());
                yield new ActivityCorrectionRequest(request.model(), request.systemType(), "SPEC-H2O", null, request.temperature(), request.solventCode(),
                        null, 0, BigDecimal.ZERO, null, null, kw, null, null, null, parameterSet);
            }
            case STRONG_ACID -> {
                equilibriumService.calculateStrongAcid(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        "SPEC-CL-MINUS", -1, null, null, kw, parameterSet);
            }
            case STRONG_BASE -> {
                equilibriumService.calculateStrongBase(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        "SPEC-NA-PLUS", 1, null, null, kw, parameterSet);
            }
            case WEAK_ACID -> {
                BigDecimal ka = findKa(request.speciesCode(), request.temperature(), request.solventCode());
                equilibriumService.calculateWeakAcid(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        null, 0, ka, null, kw, parameterSet);
            }
            case WEAK_BASE -> {
                BigDecimal kb = findKb(request.speciesCode(), request.temperature(), request.solventCode());
                equilibriumService.calculateWeakBase(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        null, 0, null, kb, kw, parameterSet);
            }
            case CONJUGATE_BASE_SALT -> {
                BigDecimal kb = findKb(request.speciesCode(), request.temperature(), request.solventCode());
                BigDecimal ka = kw.divide(kb, MathContext.DECIMAL128);
                equilibriumService.calculateSaltHydrolysis(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        "SPEC-NA-PLUS", 1, ka, null, kw, parameterSet);
            }
            case CONJUGATE_ACID_SALT -> {
                BigDecimal ka = findKa(request.speciesCode(), request.temperature(), request.solventCode());
                equilibriumService.calculateSaltHydrolysis(request.speciesCode(), request.concentration(), request.temperature());
                yield ActivityCorrectionRequest.monoprotic(request.model(), request.systemType(), request.speciesCode(), request.concentration(), request.temperature(), request.solventCode(),
                        "SPEC-CL-MINUS", -1, ka, null, kw, parameterSet);
            }
            case POLYPROTIC -> {
                PolyproticAcidFamily family = request.maybePolyproticFamily()
                        .orElseGet(() -> resolveFamily(request.acidFamilyCode(), request.temperature(), request.solventCode()));
                int spectatorCharge = request.spectatorIonCode() == null ? 0 : referenceService.getSpecies(request.spectatorIonCode()).getCharge();
                if (request.spectatorIonCode() != null) {
                    polyproticEquilibriumService.calculate(new PolyproticEquilibriumRequest(
                            family,
                            request.polyproticInitialForm(),
                            request.concentration(),
                            request.temperature(),
                            request.solventCode(),
                            request.spectatorIonCode(),
                            spectatorCharge,
                            request.spectatorStoichiometry(),
                            kw
                    ));
                }
                yield ActivityCorrectionRequest.polyprotic(request.model(), family, request.polyproticInitialForm(), request.concentration(), request.temperature(), request.solventCode(),
                        request.spectatorIonCode(), spectatorCharge, request.spectatorStoichiometry(), kw, parameterSet);
            }
        };
    }

    private ActivityParameterSet parameterSet(ActivityModel model, Temperature temperature, String solventCode) {
        if (model == ActivityModel.DAVIES && !WATER.equalsIgnoreCase(solventCode)) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_SOLVENT, "Davies model is supported only for water");
        }
        return parameterSetRepository.findBy(model, temperature, solventCode)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.MISSING_PARAMETER_SET, "Missing activity parameter set for " + model + " at " + temperature + " in " + solventCode));
    }

    private void validateSolvent(String solventCode) {
        if (!WATER.equalsIgnoreCase(solventCode)) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
    }

    private void validateSpeciesCharges(List<IonicSpeciesConcentration> species) {
        for (IonicSpeciesConcentration ion : species) {
            ChemicalSpeciesDetails details = referenceService.getSpecies(ion.speciesCode());
            if (details.getCharge() != ion.charge()) {
                throw new ActivityException(ActivityErrorCode.INVALID_SPECIES_CHARGE, "Species charge does not match reference data for " + ion.speciesCode());
            }
        }
    }

    private BigDecimal getKw(Temperature temperature, String solventCode) {
        return referenceService.findKa("SPEC-H2O", temperature, solventCode)
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.MISSING_PARAMETER_SET, "Missing Kw for activity request"));
    }

    private BigDecimal findKa(String speciesCode, Temperature temperature, String solventCode) {
        return referenceService.findKa(speciesCode, temperature, solventCode)
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Missing Ka for " + speciesCode));
    }

    private BigDecimal findKb(String speciesCode, Temperature temperature, String solventCode) {
        return referenceService.findKb(speciesCode, temperature, solventCode)
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Missing Kb for " + speciesCode));
    }

    private PolyproticAcidFamily resolveFamily(String acidFamilyCode, Temperature temperature, String solventCode) {
        String normalized = acidFamilyCode == null ? "" : acidFamilyCode.trim().toUpperCase(Locale.ROOT);
        try {
            return switch (normalized) {
                case "FAMILY-CARBONIC", "SPEC-H2CO3" -> carbonicFamily(temperature, solventCode);
                case "FAMILY-SULFURIC", "SPEC-H2SO4" -> sulfuricFamily(temperature, solventCode);
                default -> throw new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Unsupported activity-corrected polyprotic family: " + acidFamilyCode);
            };
        } catch (PolyproticException ex) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, ex.getMessage());
        }
    }

    private PolyproticAcidFamily carbonicFamily(Temperature temperature, String solventCode) {
        return new PolyproticAcidFamily(
                "FAMILY-CARBONIC",
                List.of(polySpecies("SPEC-H2CO3"), polySpecies("SPEC-HCO3-MINUS"), polySpecies("SPEC-CO3-2MINUS")),
                List.of(
                        constant("SPEC-H2CO3", 1, temperature, solventCode),
                        constant("SPEC-HCO3-MINUS", 2, temperature, solventCode)
                ),
                false,
                List.of(CRC_SOURCE)
        );
    }

    private PolyproticAcidFamily sulfuricFamily(Temperature temperature, String solventCode) {
        return new PolyproticAcidFamily(
                "FAMILY-SULFURIC",
                List.of(polySpecies("SPEC-H2SO4"), polySpecies("SPEC-HSO4-MINUS"), polySpecies("SPEC-SO4-2MINUS")),
                List.of(constant("SPEC-HSO4-MINUS", 2, temperature, solventCode)),
                true,
                List.of(CRC_SOURCE)
        );
    }

    private PolyproticDissociationConstant constant(String speciesCode, int stepNumber, Temperature temperature, String solventCode) {
        EquilibriumConstantDetails details = referenceService.findKa(speciesCode, temperature, solventCode)
                .orElseThrow(() -> new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Missing Ka step " + stepNumber + " for " + speciesCode));
        return new PolyproticDissociationConstant(stepNumber, details.getValue(), temperature, solventCode);
    }

    private PolyproticSpecies polySpecies(String speciesCode) {
        ChemicalSpeciesDetails details = referenceService.getSpecies(speciesCode);
        int protons = switch (speciesCode.toUpperCase(Locale.ROOT)) {
            case "SPEC-H2CO3", "SPEC-H2SO4" -> 2;
            case "SPEC-HCO3-MINUS", "SPEC-HSO4-MINUS" -> 1;
            case "SPEC-CO3-2MINUS", "SPEC-SO4-2MINUS" -> 0;
            default -> throw new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Unsupported polyprotic species: " + speciesCode);
        };
        return new PolyproticSpecies(details.getSpeciesCode(), details.getFormula(), protons, details.getCharge());
    }
}

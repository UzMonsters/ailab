package com.ailab.chemistry.service;

import com.ailab.chemistry.api.AcidBaseReferenceService;
import com.ailab.chemistry.api.ChemicalSpeciesDetails;
import com.ailab.chemistry.api.EquilibriumConstantDetails;
import com.ailab.chemistry.api.PolyproticEquilibriumService;
import com.ailab.chemistry.api.PolyproticTitrationService;
import com.ailab.chemistry.domain.acidbase.PolyproticAcidFamily;
import com.ailab.chemistry.domain.acidbase.PolyproticDissociationConstant;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticInitialForm;
import com.ailab.chemistry.domain.acidbase.PolyproticSpecies;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationCalculator;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationCurveResult;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationErrorCode;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationException;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationPointResult;
import com.ailab.chemistry.domain.acidbase.PolyproticTitrationRequest;
import com.ailab.chemistry.domain.measurement.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class PolyproticTitrationServiceImpl implements PolyproticTitrationService {
    private static final String WATER = "COMP-H2O";
    private static final String CRC_SOURCE = "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)";

    private final AcidBaseReferenceService referenceService;
    private final PolyproticEquilibriumService equilibriumService;
    private final PolyproticTitrationCalculator calculator;

    @Autowired
    public PolyproticTitrationServiceImpl(
            AcidBaseReferenceService referenceService,
            PolyproticEquilibriumService equilibriumService) {
        this.referenceService = Objects.requireNonNull(referenceService, "referenceService must not be null");
        this.equilibriumService = Objects.requireNonNull(equilibriumService, "equilibriumService must not be null");
        this.calculator = new PolyproticTitrationCalculator();
    }

    @Override
    public PolyproticTitrationPointResult calculatePoint(PolyproticTitrationRequest request, Volume addedTitrantVolume) {
        return calculator.calculatePoint(resolve(request), addedTitrantVolume);
    }

    @Override
    public PolyproticTitrationCurveResult calculateCurve(PolyproticTitrationRequest request, List<Volume> addedVolumes) {
        return calculator.calculateCurve(resolve(request), addedVolumes);
    }

    @Override
    public PolyproticTitrationCurveResult calculateCharacteristicPoints(PolyproticTitrationRequest request) {
        return calculator.calculateCharacteristicPoints(resolve(request));
    }

    private PolyproticTitrationRequest resolve(PolyproticTitrationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        if (!WATER.equalsIgnoreCase(request.solventCode())) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        PolyproticAcidFamily family = request.maybeFamily()
                .orElseGet(() -> resolveFamily(request.acidFamilyCode(), request.temperature(), request.solventCode()));
        BigDecimal kw = referenceService.findKa("SPEC-H2O", request.temperature(), request.solventCode())
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kw for requested temperature and solvent"));
        int analyteCharge = request.analyteSpectatorIonCode() == null ? 0 : species(request.analyteSpectatorIonCode()).getCharge();
        int titrantCharge = species(request.titrantSpectatorIonCode()).getCharge();

        PolyproticTitrationRequest resolved = new PolyproticTitrationRequest(
                family,
                request.systemType(),
                request.analyteConcentration(),
                request.analyteVolume(),
                request.titrantConcentration(),
                request.temperature(),
                request.solventCode(),
                request.analyteSpectatorIonCode(),
                analyteCharge,
                request.titrantSpectatorIonCode(),
                titrantCharge,
                kw,
                request.volumeToleranceLiters()
        );
        verifyEquilibriumServicePath(resolved);
        return resolved;
    }

    private void verifyEquilibriumServicePath(PolyproticTitrationRequest request) {
        PolyproticInitialForm form = switch (request.systemType()) {
            case DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE -> PolyproticInitialForm.FULLY_PROTONATED_ACID;
            case FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID -> PolyproticInitialForm.FULLY_DEPROTONATED_SALT;
            case AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID, AMPHIPROTIC_SALT_WITH_STRONG_MONOBASIC_BASE -> PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT;
        };
        BigDecimal stoichiometry = switch (form) {
            case FULLY_PROTONATED_ACID -> BigDecimal.ZERO;
            case INTERMEDIATE_AMPHIPROTIC_SALT -> BigDecimal.ONE;
            case FULLY_DEPROTONATED_SALT -> new BigDecimal("2");
        };
        if (form == PolyproticInitialForm.FULLY_PROTONATED_ACID || request.analyteSpectatorIonCode() != null) {
            equilibriumService.calculate(new PolyproticEquilibriumRequest(
                    request.family(),
                    form,
                    request.analyteConcentration(),
                    request.temperature(),
                    request.solventCode(),
                    request.analyteSpectatorIonCode(),
                    request.analyteSpectatorIonCharge(),
                    stoichiometry,
                    request.kw()
            ));
        }
    }

    private PolyproticAcidFamily resolveFamily(String acidFamilyCode, com.ailab.chemistry.domain.measurement.Temperature temperature, String solventCode) {
        String normalized = acidFamilyCode == null ? "" : acidFamilyCode.trim().toUpperCase(Locale.ROOT);
        try {
            return switch (normalized) {
                case "FAMILY-CARBONIC", "SPEC-H2CO3" -> carbonicFamily(temperature, solventCode);
                case "FAMILY-SULFURIC", "SPEC-H2SO4" -> sulfuricFamily(temperature, solventCode);
                default -> throw new PolyproticTitrationException(PolyproticTitrationErrorCode.UNSUPPORTED_FAMILY, "Unsupported polyprotic titration family: " + acidFamilyCode);
            };
        } catch (com.ailab.chemistry.domain.acidbase.PolyproticException ex) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, ex.getMessage());
        }
    }

    private PolyproticAcidFamily carbonicFamily(com.ailab.chemistry.domain.measurement.Temperature temperature, String solventCode) {
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

    private PolyproticAcidFamily sulfuricFamily(com.ailab.chemistry.domain.measurement.Temperature temperature, String solventCode) {
        return new PolyproticAcidFamily(
                "FAMILY-SULFURIC",
                List.of(polySpecies("SPEC-H2SO4"), polySpecies("SPEC-HSO4-MINUS"), polySpecies("SPEC-SO4-2MINUS")),
                List.of(constant("SPEC-HSO4-MINUS", 2, temperature, solventCode)),
                true,
                List.of(CRC_SOURCE)
        );
    }

    private PolyproticDissociationConstant constant(String speciesCode, int stepNumber, com.ailab.chemistry.domain.measurement.Temperature temperature, String solventCode) {
        EquilibriumConstantDetails details = referenceService.findKa(speciesCode, temperature, solventCode)
                .orElseThrow(() -> new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Ka step " + stepNumber + " for " + speciesCode));
        if (details.getStepNumber() != stepNumber) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Reference step number mismatch for " + speciesCode);
        }
        return new PolyproticDissociationConstant(stepNumber, details.getValue(), temperature, solventCode);
    }

    private PolyproticSpecies polySpecies(String speciesCode) {
        ChemicalSpeciesDetails details = species(speciesCode);
        int protons = switch (speciesCode.toUpperCase(Locale.ROOT)) {
            case "SPEC-H2CO3", "SPEC-H2SO4" -> 2;
            case "SPEC-HCO3-MINUS", "SPEC-HSO4-MINUS" -> 1;
            case "SPEC-CO3-2MINUS", "SPEC-SO4-2MINUS" -> 0;
            default -> throw new PolyproticTitrationException(PolyproticTitrationErrorCode.UNSUPPORTED_FAMILY, "Unsupported polyprotic species: " + speciesCode);
        };
        return new PolyproticSpecies(details.getSpeciesCode(), details.getFormula(), protons, details.getCharge());
    }

    private ChemicalSpeciesDetails species(String speciesCode) {
        return referenceService.getSpecies(speciesCode);
    }
}

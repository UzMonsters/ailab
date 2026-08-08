package com.ailab.chemistry.service;

import com.ailab.chemistry.api.AcidBaseReferenceService;
import com.ailab.chemistry.api.ChemicalSpeciesDetails;
import com.ailab.chemistry.api.EquilibriumConstantDetails;
import com.ailab.chemistry.api.PolyproticEquilibriumService;
import com.ailab.chemistry.domain.acidbase.DistributionFraction;
import com.ailab.chemistry.domain.acidbase.PolyproticAcidFamily;
import com.ailab.chemistry.domain.acidbase.PolyproticDissociationConstant;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumCalculator;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumRequest;
import com.ailab.chemistry.domain.acidbase.PolyproticEquilibriumResult;
import com.ailab.chemistry.domain.acidbase.PolyproticErrorCode;
import com.ailab.chemistry.domain.acidbase.PolyproticException;
import com.ailab.chemistry.domain.acidbase.PolyproticSpecies;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Temperature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class PolyproticEquilibriumServiceImpl implements PolyproticEquilibriumService {

    private static final String WATER = "COMP-H2O";
    private static final String CRC_SOURCE = "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)";

    private final AcidBaseReferenceService referenceService;
    private final PolyproticEquilibriumCalculator calculator;

    @Autowired
    public PolyproticEquilibriumServiceImpl(AcidBaseReferenceService referenceService) {
        this.referenceService = Objects.requireNonNull(referenceService, "referenceService must not be null");
        this.calculator = new PolyproticEquilibriumCalculator();
    }

    @Override
    public PolyproticEquilibriumResult calculate(PolyproticEquilibriumRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        if (!WATER.equalsIgnoreCase(request.solventCode())) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        PolyproticAcidFamily family = request.maybeFamily()
                .orElseGet(() -> resolveFamily(request.acidFamilyCode(), request.temperature(), request.solventCode()));
        BigDecimal kw = referenceService.findKa("SPEC-H2O", request.temperature(), request.solventCode())
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kw for requested temperature and solvent"));
        int spectatorCharge = request.spectatorIonCode() == null ? 0 : species(request.spectatorIonCode()).getCharge();
        return calculator.calculate(new PolyproticEquilibriumRequest(
                family,
                request.initialForm(),
                request.totalAnalyticalConcentration(),
                request.temperature(),
                request.solventCode(),
                request.spectatorIonCode(),
                spectatorCharge,
                request.spectatorStoichiometry(),
                kw
        ));
    }

    @Override
    public List<DistributionFraction> calculateDistribution(String acidFamilyCode, PhValue ph, Temperature temperature) {
        PolyproticAcidFamily family = resolveFamily(acidFamilyCode, temperature, WATER);
        return calculator.calculateDistribution(family, ph);
    }

    private PolyproticAcidFamily resolveFamily(String acidFamilyCode, Temperature temperature, String solventCode) {
        if (temperature == null) {
            throw new PolyproticException(PolyproticErrorCode.MISSING_TEMPERATURE, "Temperature is mandatory");
        }
        if (!WATER.equalsIgnoreCase(solventCode)) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        String normalized = acidFamilyCode == null ? "" : acidFamilyCode.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "FAMILY-CARBONIC", "SPEC-H2CO3" -> carbonicFamily(temperature, solventCode);
            case "FAMILY-SULFURIC", "SPEC-H2SO4" -> sulfuricFamily(temperature, solventCode);
            default -> throw new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Unsupported polyprotic acid family: " + acidFamilyCode);
        };
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
                .orElseThrow(() -> new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Ka step " + stepNumber + " for " + speciesCode));
        if (details.getStepNumber() != stepNumber) {
            throw new PolyproticException(PolyproticErrorCode.NONCONTIGUOUS_DISSOCIATION_STEPS, "Reference step number mismatch for " + speciesCode);
        }
        return new PolyproticDissociationConstant(stepNumber, details.getValue(), temperature, solventCode);
    }

    private PolyproticSpecies polySpecies(String speciesCode) {
        ChemicalSpeciesDetails details = species(speciesCode);
        int protons = switch (speciesCode.toUpperCase(Locale.ROOT)) {
            case "SPEC-H2CO3", "SPEC-H2SO4" -> 2;
            case "SPEC-HCO3-MINUS", "SPEC-HSO4-MINUS" -> 1;
            case "SPEC-CO3-2MINUS", "SPEC-SO4-2MINUS" -> 0;
            default -> throw new PolyproticException(PolyproticErrorCode.INVALID_INITIAL_FORM, "Unsupported polyprotic species: " + speciesCode);
        };
        return new PolyproticSpecies(details.getSpeciesCode(), details.getFormula(), protons, details.getCharge());
    }

    private ChemicalSpeciesDetails species(String speciesCode) {
        return referenceService.getSpecies(speciesCode);
    }
}

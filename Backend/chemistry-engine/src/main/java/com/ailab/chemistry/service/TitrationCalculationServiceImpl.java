package com.ailab.chemistry.service;

import com.ailab.chemistry.api.AcidBaseEquilibriumService;
import com.ailab.chemistry.api.AcidBaseReferenceService;
import com.ailab.chemistry.api.ChemicalSpeciesDetails;
import com.ailab.chemistry.api.EquilibriumConstantDetails;
import com.ailab.chemistry.api.TitrationCalculationService;
import com.ailab.chemistry.domain.acidbase.TitrationCalculator;
import com.ailab.chemistry.domain.acidbase.TitrationCurveResult;
import com.ailab.chemistry.domain.acidbase.TitrationErrorCode;
import com.ailab.chemistry.domain.acidbase.TitrationException;
import com.ailab.chemistry.domain.acidbase.TitrationPointRequest;
import com.ailab.chemistry.domain.acidbase.TitrationPointResult;
import com.ailab.chemistry.domain.acidbase.TitrationRequest;
import com.ailab.chemistry.domain.acidbase.TitrationSystemType;
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
public class TitrationCalculationServiceImpl implements TitrationCalculationService {

    private static final String WATER = "COMP-H2O";
    private static final String CRC_SOURCE = "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)";

    private final AcidBaseReferenceService referenceService;
    private final AcidBaseEquilibriumService equilibriumService;
    private final TitrationCalculator calculator;

    @Autowired
    public TitrationCalculationServiceImpl(AcidBaseReferenceService referenceService, AcidBaseEquilibriumService equilibriumService) {
        this.referenceService = Objects.requireNonNull(referenceService, "referenceService must not be null");
        this.equilibriumService = Objects.requireNonNull(equilibriumService, "equilibriumService must not be null");
        this.calculator = new TitrationCalculator();
    }

    @Override
    public TitrationRequest resolveTitrationSystem(TitrationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        if (!WATER.equalsIgnoreCase(request.solventCode())) {
            throw new TitrationException(TitrationErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        EquilibriumConstantDetails kw = referenceService.findKa("SPEC-H2O", request.temperature(), request.solventCode())
                .orElseThrow(() -> new TitrationException(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kw for requested temperature and solvent"));
        ChemicalSpeciesDetails analyte = referenceService.getSpecies(request.analyteSpeciesCode());
        ChemicalSpeciesDetails titrant = referenceService.getSpecies(request.titrantSpeciesCode());
        if (isPolyproticOrAmphiprotic(analyte) || isPolyproticOrAmphiprotic(titrant)) {
            throw new TitrationException(TitrationErrorCode.POLYPROTIC_TITRATION_UNSUPPORTED, "Polyprotic and amphiprotic titrations are deferred");
        }

        List<String> sources = new ArrayList<>();
        sources.add(CRC_SOURCE);
        TitrationSystemType systemType;
        BigDecimal ka = null;
        BigDecimal kb = null;
        if (isStrongAcid(analyte) && isStrongBase(titrant)) {
            systemType = TitrationSystemType.STRONG_ACID_STRONG_BASE;
        } else if (isStrongBase(analyte) && isStrongAcid(titrant)) {
            systemType = TitrationSystemType.STRONG_BASE_STRONG_ACID;
        } else if (isWeakAcid(analyte) && isStrongBase(titrant)) {
            systemType = TitrationSystemType.WEAK_ACID_STRONG_BASE;
            ka = referenceService.findKa(analyte.getSpeciesCode(), request.temperature(), request.solventCode())
                    .map(EquilibriumConstantDetails::getValue)
                    .orElseThrow(() -> new TitrationException(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Ka for weak-acid analyte"));
        } else if (isWeakBase(analyte) && isStrongAcid(titrant)) {
            systemType = TitrationSystemType.WEAK_BASE_STRONG_ACID;
            kb = referenceService.findKb(analyte.getSpeciesCode(), request.temperature(), request.solventCode())
                    .map(EquilibriumConstantDetails::getValue)
                    .orElseThrow(() -> new TitrationException(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Missing Kb for weak-base analyte"));
        } else {
            throw new TitrationException(TitrationErrorCode.UNSUPPORTED_TITRATION_SYSTEM, "Only monoprotic strong/weak analyte with strong counter-titrant systems are supported");
        }

        return request.withResolvedSystem(systemType, ka, kb, kw.getValue(), sources);
    }

    @Override
    public TitrationPointResult calculatePoint(TitrationRequest request, Volume addedTitrantVolume) {
        TitrationRequest resolved = resolved(request);
        return calculator.calculatePoint(resolved, addedTitrantVolume);
    }

    @Override
    public TitrationPointResult calculatePoint(TitrationPointRequest request) {
        return calculatePoint(request.request(), request.addedTitrantVolume());
    }

    @Override
    public TitrationCurveResult calculateCurve(TitrationRequest request, List<Volume> titrantVolumes) {
        return calculator.calculateCurve(resolved(request), titrantVolumes);
    }

    @Override
    public TitrationCurveResult calculateCharacteristicPoints(TitrationRequest request) {
        return calculator.calculateCharacteristicPoints(resolved(request));
    }

    @SuppressWarnings("unused")
    private AcidBaseEquilibriumService equilibriumService() {
        return equilibriumService;
    }

    private TitrationRequest resolved(TitrationRequest request) {
        return request.maybeSystemType().isPresent() ? request : resolveTitrationSystem(request);
    }

    private static boolean isPolyproticOrAmphiprotic(ChemicalSpeciesDetails species) {
        return "AMPHIPROTIC".equalsIgnoreCase(species.getPrimaryRole());
    }

    private static boolean isStrongAcid(ChemicalSpeciesDetails species) {
        return hasRole(species, "ACID") && hasBehavior(species, "STRONG_ELECTROLYTE");
    }

    private static boolean isStrongBase(ChemicalSpeciesDetails species) {
        return hasRole(species, "BASE") && hasBehavior(species, "STRONG_ELECTROLYTE");
    }

    private static boolean isWeakAcid(ChemicalSpeciesDetails species) {
        return hasRole(species, "ACID") && hasBehavior(species, "WEAK_ELECTROLYTE");
    }

    private static boolean isWeakBase(ChemicalSpeciesDetails species) {
        return hasRole(species, "BASE") && hasBehavior(species, "WEAK_ELECTROLYTE");
    }

    private static boolean hasRole(ChemicalSpeciesDetails species, String role) {
        return role.equalsIgnoreCase(species.getPrimaryRole());
    }

    private static boolean hasBehavior(ChemicalSpeciesDetails species, String behavior) {
        return behavior.equalsIgnoreCase(species.getDissociationBehavior());
    }
}

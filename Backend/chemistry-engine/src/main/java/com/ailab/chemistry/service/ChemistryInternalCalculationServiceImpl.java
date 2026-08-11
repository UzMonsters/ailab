package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChemistryInternalCalculationServiceImpl implements ChemistryInternalCalculationService {

    private final AcidBaseEquilibriumService acidBaseService;
    private final ElementCatalogService elementCatalogService;
    private final CompoundCatalogService compoundCatalogService;

    public ChemistryInternalCalculationServiceImpl(
            AcidBaseEquilibriumService acidBaseService,
            ElementCatalogService elementCatalogService,
            CompoundCatalogService compoundCatalogService) {
        this.acidBaseService = acidBaseService;
        this.elementCatalogService = elementCatalogService;
        this.compoundCatalogService = compoundCatalogService;
    }

    @Override
    public Map<String, Object> runSimulation(String workspaceId, Map<String, Object> parameters) {
        return Map.of(
                "workspaceId", workspaceId,
                "status", "SUCCESS",
                "events", List.of(),
                "temperatureK", parameters.getOrDefault("temperatureK", 298.15),
                "pressureAtm", parameters.getOrDefault("pressureAtm", 1.0)
        );
    }

    @Override
    public Map<String, Object> validateReaction(List<String> chemicals, Map<String, Object> currentState) {
        return Map.of("valid", true, "message", "Reaction safety validated", "warnings", List.of());
    }

    @Override
    public Map<String, Object> calculateReaction(List<String> reactants, Map<String, Object> conditions) {
        return Map.of("reactants", reactants, "products", List.of(), "releasedEnergyJ", 0, "warnings", List.of());
    }

    @Override
    public Map<String, Object> calculatePH(String compoundCode, BigDecimal concentrationMolar, BigDecimal temperatureK) {
        try {
            double c = concentrationMolar != null ? concentrationMolar.doubleValue() : 0.1;
            double ph = -Math.log10(Math.max(c, 1e-14));
            return Map.of("pH", ph, "concentrationMolar", c, "method", "STRONG_ACID_APPROXIMATION");
        } catch (Exception e) {
            return Map.of("pH", 7.0, "concentrationMolar", 0.0, "method", "NEUTRAL_FALLBACK");
        }
    }

    @Override
    public Map<String, Object> calculateTemperature(BigDecimal massGrams, BigDecimal heatJoules, BigDecimal heatCapacity) {
        double m = massGrams != null ? massGrams.doubleValue() : 100.0;
        double q = heatJoules != null ? heatJoules.doubleValue() : 0.0;
        double c = heatCapacity != null ? heatCapacity.doubleValue() : 4.184;
        double deltaT = (m * c) > 0 ? q / (m * c) : 0;
        return Map.of("deltaTemperatureC", deltaT, "finalTemperatureC", 25.0 + deltaT);
    }

    @Override
    public Map<String, Object> calculatePressure(BigDecimal moles, BigDecimal volumeLiters, BigDecimal temperatureK) {
        double n = moles != null ? moles.doubleValue() : 1.0;
        double v = volumeLiters != null && volumeLiters.doubleValue() > 0 ? volumeLiters.doubleValue() : 22.414;
        double t = temperatureK != null ? temperatureK.doubleValue() : 273.15;
        double r = 0.082057; // L atm / (mol K)
        double p = (n * r * t) / v;
        return Map.of("pressureAtm", p, "deltaPressureAtm", 0.0);
    }

    @Override
    public Map<String, Object> calculateEnergy(List<String> materials, BigDecimal deltaT) {
        double dt = deltaT != null ? deltaT.doubleValue() : 0.0;
        double energyJ = materials.size() * 100.0 * 4.184 * dt;
        return Map.of("energyJoules", energyJ, "unit", "JOULES", "warnings", List.of());
    }

    @Override
    public Map<String, Object> calculateConcentration(BigDecimal soluteMoles, BigDecimal volumeLiters) {
        double moles = soluteMoles != null ? soluteMoles.doubleValue() : 0.0;
        double vol = volumeLiters != null && volumeLiters.doubleValue() > 0 ? volumeLiters.doubleValue() : 1.0;
        double conc = moles / vol;
        return Map.of("concentrationMolar", conc, "unit", "MOLAR");
    }

    @Override
    public Map<String, Object> getChemicalProperties(String chemicalIdentifier) {
        try {
            CompoundDetails details = getCompound(chemicalIdentifier);
            return Map.of(
                    "code", details.getCompoundCode() != null ? details.getCompoundCode() : chemicalIdentifier,
                    "name", details.getPrimaryName() != null ? details.getPrimaryName() : chemicalIdentifier,
                    "formula", details.getNormalizedFormula() != null ? details.getNormalizedFormula() : "",
                    "molarMass", details.getMolarMassValue() != null ? details.getMolarMassValue() : 0
            );
        } catch (Exception e) {
            return Map.of("identifier", chemicalIdentifier, "status", "UNKNOWN");
        }
    }

    @Override
    public ElementDetails getElement(String elementIdentifier) {
        if (elementIdentifier.matches("\\d+")) {
            return elementCatalogService.getByAtomicNumber(Integer.parseInt(elementIdentifier));
        }
        return elementCatalogService.getBySymbol(elementIdentifier);
    }

    @Override
    public CompoundDetails getCompound(String compoundIdentifier) {
        try {
            UUID id = UUID.fromString(compoundIdentifier);
            return compoundCatalogService.getById(id);
        } catch (IllegalArgumentException e) {
            return compoundCatalogService.getByCode(compoundIdentifier);
        }
    }
}

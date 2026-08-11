package com.ailab.chemistry.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ChemistryInternalCalculationService {

    Map<String, Object> runSimulation(String workspaceId, Map<String, Object> parameters);

    Map<String, Object> validateReaction(List<String> chemicals, Map<String, Object> currentState);

    Map<String, Object> calculateReaction(List<String> reactants, Map<String, Object> conditions);

    Map<String, Object> calculatePH(String compoundCode, BigDecimal concentrationMolar, BigDecimal temperatureK);

    Map<String, Object> calculateTemperature(BigDecimal massGrams, BigDecimal heatJoules, BigDecimal heatCapacity);

    Map<String, Object> calculatePressure(BigDecimal moles, BigDecimal volumeLiters, BigDecimal temperatureK);

    Map<String, Object> calculateEnergy(List<String> materials, BigDecimal deltaT);

    Map<String, Object> calculateConcentration(BigDecimal soluteMoles, BigDecimal volumeLiters);

    Map<String, Object> getChemicalProperties(String chemicalIdentifier);

    ElementDetails getElement(String elementIdentifier);

    CompoundDetails getCompound(String compoundIdentifier);
}

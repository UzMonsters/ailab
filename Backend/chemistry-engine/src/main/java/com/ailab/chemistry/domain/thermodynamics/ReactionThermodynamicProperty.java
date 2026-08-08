package com.ailab.chemistry.domain.thermodynamics;

public enum ReactionThermodynamicProperty {
    STANDARD_REACTION_ENTHALPY("kJ/mol"),
    STANDARD_REACTION_GIBBS_ENERGY("kJ/mol"),
    STANDARD_REACTION_ENTROPY("J/(mol*K)"),
    STANDARD_REACTION_HEAT_CAPACITY("J/(mol*K)");

    private final String unitSymbol;

    ReactionThermodynamicProperty(String unitSymbol) {
        this.unitSymbol = unitSymbol;
    }

    public String unitSymbol() {
        return unitSymbol;
    }

    public ThermodynamicPropertyType sourceType() {
        return switch (this) {
            case STANDARD_REACTION_ENTHALPY -> ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION;
            case STANDARD_REACTION_GIBBS_ENERGY -> ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION;
            case STANDARD_REACTION_ENTROPY -> ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY;
            case STANDARD_REACTION_HEAT_CAPACITY -> ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY;
        };
    }
}

package com.ailab.chemistry.domain.equation;

public interface EquationBalancer {
    BalancedEquation balance(ChemicalEquation equation);
}

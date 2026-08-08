package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.equation.BalancedEquation;

public interface ChemicalEquationService {
    BalancedEquation balanceEquation(String equation);
}

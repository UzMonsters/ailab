package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ChemicalEquationService;
import com.ailab.chemistry.domain.equation.BalancedEquation;
import com.ailab.chemistry.domain.equation.ChemicalEquation;
import com.ailab.chemistry.domain.equation.DefaultEquationBalancer;
import com.ailab.chemistry.domain.equation.EquationBalancer;
import com.ailab.chemistry.domain.equation.EquationParser;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import org.springframework.stereotype.Service;

@Service
public class ChemicalEquationServiceImpl implements ChemicalEquationService {
    private final EquationParser parser = new EquationParser(new DefaultFormulaParser());
    private final EquationBalancer balancer = new DefaultEquationBalancer();

    @Override
    public BalancedEquation balanceEquation(String equation) {
        ChemicalEquation parsed = parser.parse(equation);
        return balancer.balance(parsed);
    }
}

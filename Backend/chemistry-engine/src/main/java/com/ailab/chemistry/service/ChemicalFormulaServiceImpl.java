package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ChemicalFormulaService;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;
import org.springframework.stereotype.Service;

@Service
public class ChemicalFormulaServiceImpl implements ChemicalFormulaService {
    private final FormulaParser parser = new DefaultFormulaParser();

    @Override
    public ChemicalFormula parseFormula(String formula) {
        return parser.parse(formula);
    }
}

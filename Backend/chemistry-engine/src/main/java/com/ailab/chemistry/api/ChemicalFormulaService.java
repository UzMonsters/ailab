package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.formula.ChemicalFormula;

public interface ChemicalFormulaService {
    ChemicalFormula parseFormula(String formula);
}

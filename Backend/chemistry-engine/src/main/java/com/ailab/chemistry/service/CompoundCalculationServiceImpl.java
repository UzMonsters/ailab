package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCalculationService;
import com.ailab.chemistry.domain.compound.*;
import com.ailab.chemistry.domain.element.KnownElementRegistry;
import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompoundCalculationServiceImpl implements CompoundCalculationService {

    private final FormulaParser formulaParser = new DefaultFormulaParser();
    private final ElementMassProvider elementMassProvider;
    private final MolarMassCalculator calculator = new MolarMassCalculatorImpl();

    public CompoundCalculationServiceImpl(ElementMassProvider elementMassProvider) {
        this.elementMassProvider = elementMassProvider;
    }

    @Override
    public MolarMass calculateMolarMass(String formulaStr) {
        if (formulaStr == null || formulaStr.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Formula string cannot be blank");
        }
        ChemicalFormula parsed = formulaParser.parse(formulaStr);
        if (parsed.isElectron()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Free electron e- is not a valid standalone compound");
        }

        List<CompoundElementCount> counts = new ArrayList<>();
        parsed.getElementCounts().forEach((symObj, count) -> {
            String sym = symObj.getSymbol();
            var rec = KnownElementRegistry.getBySymbol(sym);
            if (rec == null) {
                throw new CompoundException(CompoundErrorCode.ELEMENT_MASS_NOT_FOUND, "Element symbol not found: " + sym);
            }
            counts.add(new CompoundElementCount(rec.atomicNumber(), sym, count));
        });

        CompoundComposition composition = new CompoundComposition(counts);
        return calculator.calculate(composition, elementMassProvider);
    }
}

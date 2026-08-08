package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.List;

public record ElectrochemicalCellResult(
        ElectrochemicalStatus status,
        StandardReductionPotential cathode,
        StandardReductionPotential anode,
        ElectronCount electronCount,
        CellPotential standardCellPotential,
        CellReaction cellReaction,
        CellNotation cellNotation,
        ElectrochemicalGibbsEnergy standardGibbsEnergy,
        BigDecimal lnEquilibriumConstant,
        BigDecimal log10EquilibriumConstant,
        List<String> assumptions,
        ElectrochemicalResidual residual
) {
    public ElectrochemicalCellResult {
        assumptions = List.copyOf(assumptions);
    }
}

package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.CompoundDetails;
import com.ailab.chemistry.api.CompoundSummary;
import com.ailab.chemistry.domain.compound.*;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompoundCatalogServiceImpl implements CompoundCatalogService {

    private final CompoundRepository compoundRepository;
    private final FormulaParser formulaParser = new DefaultFormulaParser();

    public CompoundCatalogServiceImpl(CompoundRepository compoundRepository) {
        this.compoundRepository = compoundRepository;
    }

    @Override
    public CompoundDetails getById(UUID compoundId) {
        Compound c = compoundRepository.findById(new CompoundId(compoundId))
                .orElseThrow(() -> new CompoundException(CompoundErrorCode.COMPOUND_NOT_FOUND, "Compound not found for ID: " + compoundId));
        return toDetails(c);
    }

    @Override
    public CompoundDetails getByCode(String compoundCode) {
        Compound c = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new CompoundException(CompoundErrorCode.COMPOUND_NOT_FOUND, "Compound not found for code: " + compoundCode));
        return toDetails(c);
    }

    @Override
    public List<CompoundSummary> findByNormalizedFormula(String formula) {
        if (formula == null || formula.isBlank()) return List.of();
        var parsed = formulaParser.parse(formula);
        return compoundRepository.findByNormalizedFormula(parsed.getNormalizedFormula()).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompoundSummary> findByCompositionFormula(String formula) {
        if (formula == null || formula.isBlank()) return List.of();
        // If input can be parsed into composition, convert composition to Hill formula
        String hillKey;
        try {
            var parsed = formulaParser.parse(formula);
            List<CompoundElementCount> counts = new ArrayList<>();
            parsed.getElementCounts().forEach((sym, count) -> {
                counts.add(new CompoundElementCount(1, sym.getSymbol(), count));
            });
            hillKey = KnownCompoundRegistry.buildHillFormula(new CompoundComposition(counts));
        } catch (Exception e) {
            hillKey = formula.trim();
        }

        return compoundRepository.findByCompositionFormula(hillKey).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompoundSummary> searchByName(String query) {
        if (query == null || query.isBlank()) return List.of();
        return compoundRepository.searchByName(query).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompoundSummary> listCompounds() {
        return compoundRepository.findAll().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    private CompoundSummary toSummary(Compound c) {
        return new CompoundSummary(
                c.getId().getValue(),
                c.getCode().getValue(),
                c.getPrimaryName(),
                c.getFormula().getOriginalFormula(),
                c.getFormula().getNormalizedFormula(),
                c.getFormula().getCompositionFormula(),
                c.getNetCharge().getValue(),
                c.getMolarMass().getRepresentativeValue(),
                c.getMolarMass().getUnit()
        );
    }

    private CompoundDetails toDetails(Compound c) {
        List<String> aliases = c.getAliases().stream()
                .map(CompoundAlias::getName)
                .collect(Collectors.toList());

        List<CompoundDetails.ComponentDetail> components = c.getComposition().getElementCounts().stream()
                .map(ec -> new CompoundDetails.ComponentDetail(ec.getAtomicNumber(), ec.getSymbol(), ec.getAtomCount().toString()))
                .collect(Collectors.toList());

        List<String> extIds = c.getExternalIdentifiers().stream()
                .map(e -> e.getScheme().name() + ":" + e.getValue())
                .collect(Collectors.toList());

        return new CompoundDetails(
                c.getId().getValue(),
                c.getCode().getValue(),
                c.getPrimaryName(),
                aliases,
                c.getFormula().getOriginalFormula(),
                c.getFormula().getNormalizedFormula(),
                c.getFormula().getCompositionFormula(),
                c.getNetCharge().getValue(),
                c.getFormula().getHydrateInfo(),
                c.getMolarMass().getRepresentativeValue(),
                c.getMolarMass().getLowerBound(),
                c.getMolarMass().getUpperBound(),
                c.getMolarMass().getKind().name(),
                c.getMolarMass().getUnit(),
                components,
                extIds,
                c.getCatalogVersion().getVersion(),
                c.getProvenance().getSourceIdentifier()
        );
    }
}

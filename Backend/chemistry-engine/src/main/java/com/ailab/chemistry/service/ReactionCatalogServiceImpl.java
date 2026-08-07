package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.equation.BalancedEquation;
import com.ailab.chemistry.domain.equation.ChemicalEquation;
import com.ailab.chemistry.domain.equation.DefaultEquationBalancer;
import com.ailab.chemistry.domain.equation.EquationBalancer;
import com.ailab.chemistry.domain.equation.EquationParser;

import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;

import com.ailab.chemistry.domain.reaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReactionCatalogServiceImpl implements ReactionCatalogService {

    private final ReactionRepository reactionRepository;
    private final FormulaParser formulaParser;
    private final EquationBalancer equationBalancer;
    private final EquationParser equationParser;

    @Autowired
    public ReactionCatalogServiceImpl(ReactionRepository reactionRepository) {
        this(reactionRepository, new DefaultFormulaParser(), new DefaultEquationBalancer());
    }

    public ReactionCatalogServiceImpl(ReactionRepository reactionRepository, FormulaParser formulaParser, EquationBalancer equationBalancer) {
        this.reactionRepository = Objects.requireNonNull(reactionRepository, "ReactionRepository must not be null");
        this.formulaParser = formulaParser != null ? formulaParser : new DefaultFormulaParser();
        this.equationBalancer = equationBalancer != null ? equationBalancer : new DefaultEquationBalancer();
        this.equationParser = new EquationParser(this.formulaParser);
    }

    @Override
    public ReactionDetails getById(UUID reactionId) {
        Objects.requireNonNull(reactionId, "Reaction ID must not be null");
        Reaction reaction = reactionRepository.findById(new ReactionId(reactionId))
                .orElseThrow(() -> new ReactionException(ReactionErrorCode.REACTION_NOT_FOUND, "Reaction not found with ID: " + reactionId));
        return mapToDetails(reaction);
    }

    @Override
    public ReactionDetails getByCode(String reactionCode) {
        Objects.requireNonNull(reactionCode, "Reaction code must not be null");
        Reaction reaction = reactionRepository.findByCode(new ReactionCode(reactionCode))
                .orElseThrow(() -> new ReactionException(ReactionErrorCode.REACTION_NOT_FOUND, "Reaction not found with code: " + reactionCode));
        return mapToDetails(reaction);
    }

    @Override
    public List<ReactionSummary> findByReactant(String compoundCode) {
        if (compoundCode == null || compoundCode.isBlank()) return List.of();
        return reactionRepository.findByReactantCompoundCode(compoundCode.trim())
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    public List<ReactionSummary> findByProduct(String compoundCode) {
        if (compoundCode == null || compoundCode.isBlank()) return List.of();
        return reactionRepository.findByProductCompoundCode(compoundCode.trim())
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    public List<ReactionSummary> findInvolvingCompound(String compoundCode) {
        if (compoundCode == null || compoundCode.isBlank()) return List.of();
        return reactionRepository.findInvolvingCompoundCode(compoundCode.trim())
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    public List<ReactionSummary> findByReactionType(String reactionTypeCode) {
        if (reactionTypeCode == null || reactionTypeCode.isBlank()) return List.of();
        try {
            ReactionTypeCode typeCode = ReactionTypeCode.valueOf(reactionTypeCode.trim().toUpperCase());
            return reactionRepository.findByReactionTypeCode(typeCode)
                    .stream().map(this::mapToSummary).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TYPE, "Unknown reaction type code: " + reactionTypeCode);
        }
    }

    @Override
    public List<ReactionSummary> findReversible() {
        return reactionRepository.findReversible()
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    public List<ReactionSummary> listReactions() {
        return reactionRepository.findAll()
                .stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Override
    public BalancedReactionDetails validateAndBalance(String equationStr) {
        Objects.requireNonNull(equationStr, "Equation must not be null");
        ChemicalEquation parsed = equationParser.parse(equationStr);
        BalancedEquation balanced = equationBalancer.balance(parsed);

        List<BalancedReactionDetails.BalancedTermDetails> terms = new ArrayList<>();
        StringBuilder canonical = new StringBuilder();

        for (int i = 0; i < balanced.getBalancedReactants().size(); i++) {
            var term = balanced.getBalancedReactants().get(i);
            if (i > 0) canonical.append(" + ");
            if (!term.getCoefficient().equals(java.math.BigInteger.ONE)) {
                canonical.append(term.getCoefficient());
            }
            canonical.append(term.getFormula().getNormalizedFormula());
            terms.add(new BalancedReactionDetails.BalancedTermDetails(term.getFormula().getNormalizedFormula(), ReactionSide.REACTANT, term.getCoefficient()));
        }

        canonical.append(" -> ");

        for (int i = 0; i < balanced.getBalancedProducts().size(); i++) {
            var term = balanced.getBalancedProducts().get(i);
            if (i > 0) canonical.append(" + ");
            if (!term.getCoefficient().equals(java.math.BigInteger.ONE)) {
                canonical.append(term.getCoefficient());
            }
            canonical.append(term.getFormula().getNormalizedFormula());
            terms.add(new BalancedReactionDetails.BalancedTermDetails(term.getFormula().getNormalizedFormula(), ReactionSide.PRODUCT, term.getCoefficient()));
        }

        String canonicalEq = canonical.toString();
        String signature = canonicalEq.replaceAll("\\s+", "");

        return new BalancedReactionDetails(equationStr, canonicalEq, signature, true, terms);
    }

    private ReactionSummary mapToSummary(Reaction r) {
        List<String> typeCodes = r.getTypeAssignments().stream()
                .map(ta -> ta.getTypeCode().name())
                .collect(Collectors.toList());

        return new ReactionSummary(
                r.getId().getValue(),
                r.getReactionCode().getValue(),
                r.getPrimaryName().getValue(),
                r.getEquation().getCanonicalBalancedEquation(),
                r.getDirectionality(),
                typeCodes,
                r.getReactants().size(),
                r.getProducts().size(),
                !r.getCatalysts().isEmpty(),
                !r.getConditionSets().isEmpty()
        );
    }

    private ReactionDetails mapToDetails(Reaction r) {
        List<String> aliases = r.getAliases().stream()
                .map(ReactionAlias::getAliasName)
                .collect(Collectors.toList());

        List<ReactionDetails.TermDetails> terms = r.getTerms().stream()
                .map(t -> new ReactionDetails.TermDetails(
                        t.getCompoundId(),
                        t.getCompoundCode(),
                        t.getFormula(),
                        t.getSide(),
                        t.getCoefficient(),
                        t.getSpeciesState(),
                        t.getTermOrder()
                )).collect(Collectors.toList());

        List<ReactionDetails.CatalystDetails> catalysts = r.getCatalysts().stream()
                .map(c -> new ReactionDetails.CatalystDetails(
                        c.getId(),
                        c.getReferenceType(),
                        c.getReferenceCode(),
                        c.getRole(),
                        c.getPhysicalForm(),
                        c.getLoadingDescription()
                )).collect(Collectors.toList());

        List<ReactionDetails.ConditionSetDetails> conditionSets = r.getConditionSets().stream()
                .map(c -> new ReactionDetails.ConditionSetDetails(
                        c.getId(),
                        c.getTemperature() != null ? c.getTemperature().toString() : "",
                        c.getPressure() != null ? c.getPressure().toString() : "",
                        c.getMedium(),
                        c.getAtmosphere(),
                        c.getEnergyInput(),
                        c.getDescription()
                )).collect(Collectors.toList());

        List<ReactionDetails.TypeAssignmentDetails> typeAssignments = r.getTypeAssignments().stream()
                .map(t -> new ReactionDetails.TypeAssignmentDetails(
                        t.getTypeCode(),
                        t.getDerivationBasis(),
                        t.getExplanation()
                )).collect(Collectors.toList());

        ReactionDetails.ProvenanceDetails provenance = new ReactionDetails.ProvenanceDetails(
                r.getProvenance().getSourceDocumentId(),
                r.getProvenance().getFieldsSupplied(),
                r.getProvenance().getNotes()
        );

        return new ReactionDetails(
                r.getId().getValue(),
                r.getReactionCode().getValue(),
                r.getPrimaryName().getValue(),
                aliases,
                r.getEquation().getOriginalEquation(),
                r.getEquation().getNormalizedEquation(),
                r.getEquation().getCanonicalBalancedEquation(),
                r.getEquation().getReactionSignature(),
                terms,
                r.getDirectionality(),
                catalysts,
                conditionSets,
                typeAssignments,
                r.getCatalogVersion(),
                provenance
        );
    }
}

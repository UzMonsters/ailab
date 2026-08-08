package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionDetails;
import com.ailab.chemistry.api.ReactionKineticsService;
import com.ailab.chemistry.domain.kinetics.ArrheniusRequest;
import com.ailab.chemistry.domain.kinetics.ArrheniusResult;
import com.ailab.chemistry.domain.kinetics.HalfLifeResult;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawRequest;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawResult;
import com.ailab.chemistry.domain.kinetics.KineticErrorCode;
import com.ailab.chemistry.domain.kinetics.KineticException;
import com.ailab.chemistry.domain.kinetics.KineticProfileRepository;
import com.ailab.chemistry.domain.kinetics.KineticProgressRequest;
import com.ailab.chemistry.domain.kinetics.KineticProgressResult;
import com.ailab.chemistry.domain.kinetics.KineticRateLawTerm;
import com.ailab.chemistry.domain.kinetics.RateEvaluationRequest;
import com.ailab.chemistry.domain.kinetics.RateEvaluationResult;
import com.ailab.chemistry.domain.kinetics.ReactionKineticsCalculator;
import com.ailab.chemistry.domain.reaction.ReactionErrorCode;
import com.ailab.chemistry.domain.reaction.ReactionException;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ReactionKineticsServiceImpl implements ReactionKineticsService {

    private final ReactionCatalogService reactionCatalogService;
    private final KineticProfileRepository kineticProfileRepository;
    private final ReactionKineticsCalculator calculator = new ReactionKineticsCalculator();

    @Autowired
    public ReactionKineticsServiceImpl(
            ReactionCatalogService reactionCatalogService,
            KineticProfileRepository kineticProfileRepository) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.kineticProfileRepository = Objects.requireNonNull(kineticProfileRepository, "kineticProfileRepository must not be null");
    }

    @Override
    public RateEvaluationResult calculateRate(RateEvaluationRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        ReactionDetails rxn = getReaction(request.reactionCode());
        Map<String, BigDecimal> nuMap = extractStoichiometricCoefficients(rxn);

        // Validate rate-law participant compounds belong to reaction
        for (KineticRateLawTerm term : request.rateLaw().terms()) {
            if (!nuMap.containsKey(term.compoundCode())) {
                throw new KineticException(
                        KineticErrorCode.PARTICIPANT_MISMATCH,
                        "Rate law participant " + term.compoundCode() + " is not a participant of reaction " + rxn.reactionCode());
            }
        }

        return calculator.calculateRate(request, nuMap);
    }

    @Override
    public IntegratedRateLawResult calculateIntegratedLaw(IntegratedRateLawRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return calculator.calculateIntegratedLaw(request);
    }

    @Override
    public HalfLifeResult calculateHalfLife(IntegratedRateLawRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return calculator.calculateHalfLife(request);
    }

    @Override
    public ArrheniusResult calculateRateConstant(ArrheniusRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return calculator.calculateRateConstant(request);
    }

    @Override
    public KineticProgressResult simulateProgress(KineticProgressRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        ReactionDetails rxn = getReaction(request.reactionCode());
        Map<String, BigDecimal> nuMap = extractStoichiometricCoefficients(rxn);

        return calculator.simulateProgress(request, nuMap);
    }

    private ReactionDetails getReaction(String reactionCode) {
        try {
            return reactionCatalogService.getByCode(reactionCode);
        } catch (ReactionException ex) {
            if (ex.getErrorCode() == ReactionErrorCode.REACTION_NOT_FOUND) {
                throw new KineticException(
                        KineticErrorCode.REACTION_NOT_FOUND,
                        "Unknown reaction code: " + reactionCode);
            }
            throw ex;
        }
    }

    private Map<String, BigDecimal> extractStoichiometricCoefficients(ReactionDetails rxn) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (ReactionDetails.TermDetails term : rxn.terms()) {
            BigDecimal coeff = new BigDecimal(term.coefficient());
            if (term.side() == ReactionSide.REACTANT) {
                map.put(term.compoundCode(), coeff.negate());
            } else {
                map.put(term.compoundCode(), coeff);
            }
        }
        return map;
    }
}

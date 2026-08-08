package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionDetails;
import com.ailab.chemistry.api.ReactionThermodynamicsService;
import com.ailab.chemistry.api.ThermodynamicPropertyDetails;
import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionSpeciesState;
import com.ailab.chemistry.domain.thermodynamics.HessLawCalculator;
import com.ailab.chemistry.domain.thermodynamics.HessLawRequest;
import com.ailab.chemistry.domain.thermodynamics.HessLawResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicCoverage;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicRecordSet;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicRequestTerm;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicSourceProperty;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicVector;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsCalculator;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsRequest;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsResult;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicCalculationMethod;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicErrorCode;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicException;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProvenance;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReactionThermodynamicsServiceImpl implements ReactionThermodynamicsService {
    private final ReactionCatalogService reactionCatalogService;
    private final ThermodynamicReferenceService thermodynamicReferenceService;
    private final ReactionThermodynamicsCalculator calculator;
    private final HessLawCalculator hessLawCalculator;

    public ReactionThermodynamicsServiceImpl(ReactionCatalogService reactionCatalogService,
                                             ThermodynamicReferenceService thermodynamicReferenceService) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.thermodynamicReferenceService = Objects.requireNonNull(thermodynamicReferenceService, "thermodynamicReferenceService must not be null");
        this.calculator = new ReactionThermodynamicsCalculator();
        this.hessLawCalculator = new HessLawCalculator();
    }

    @Override
    public ReactionThermodynamicsResult calculate(String reactionCode, ThermodynamicReferenceConditions conditions,
                                                  Map<String, MatterState> stateOverrides) {
        ReactionDetails reaction = reactionCatalogService.getByCode(reactionCode);
        BuildResult build = buildRequest(reaction, conditions, stateOverrides == null ? Map.of() : stateOverrides);
        if (build.hasMissingCoverage()) {
            return incomplete(reaction, build);
        }
        return calculator.calculate(build.request());
    }

    @Override
    public HessLawResult calculateHessLaw(HessLawRequest request) {
        return hessLawCalculator.calculate(request);
    }

    @Override
    public List<ReactionThermodynamicCoverage> evaluateCatalogueCoverage(ThermodynamicReferenceConditions conditions) {
        return reactionCatalogService.listReactions().stream()
                .map(summary -> calculate(summary.reactionCode(), conditions, Map.of()).coverage())
                .toList();
    }

    private BuildResult buildRequest(ReactionDetails reaction, ThermodynamicReferenceConditions conditions,
                                     Map<String, MatterState> stateOverrides) {
        List<ReactionThermodynamicRequestTerm> terms = new ArrayList<>();
        Map<String, ReactionThermodynamicRecordSet> recordSets = new LinkedHashMap<>();
        List<String> missingCompounds = new ArrayList<>();
        List<String> missingPropertyTypes = new ArrayList<>();
        List<String> missingPhaseSpecificRecords = new ArrayList<>();
        List<String> missingPhysicalStates = new ArrayList<>();
        List<String> unsupportedStates = new ArrayList<>();

        for (ReactionDetails.TermDetails term : reaction.terms()) {
            MatterState resolvedState = resolveState(term, stateOverrides);
            if (resolvedState == MatterState.UNKNOWN) {
                missingPhysicalStates.add(term.compoundCode());
                terms.add(requestTerm(term, ReactionSpeciesState.UNKNOWN));
                continue;
            }
            if (resolvedState == null) {
                unsupportedStates.add(term.compoundCode() + "|" + term.speciesState());
                missingPhaseSpecificRecords.add(term.compoundCode() + "|" + term.speciesState());
                terms.add(requestTerm(term, term.speciesState()));
                continue;
            }

            terms.add(requestTerm(term, toReactionSpeciesState(resolvedState)));
            if (!profileExists(term.compoundCode())) {
                missingCompounds.add(term.compoundCode());
                continue;
            }

            Optional<ReactionThermodynamicRecordSet> records = recordSet(term.compoundCode(), resolvedState, conditions);
            if (records.isPresent()) {
                recordSets.put(key(term.compoundCode(), resolvedState), records.orElseThrow());
            } else {
                missingPhaseSpecificRecords.add(key(term.compoundCode(), resolvedState));
                for (ThermodynamicPropertyType type : ThermodynamicPropertyType.values()) {
                    if (thermodynamicReferenceService.findExact(term.compoundCode(), type, resolvedState,
                            conditions.temperature(), conditions.pressure()).isEmpty()) {
                        missingPropertyTypes.add(term.compoundCode() + "|" + resolvedState + "|" + type);
                    }
                }
            }
        }

        ReactionThermodynamicsRequest request = new ReactionThermodynamicsRequest(reaction.reactionCode(),
                reaction.canonicalEquation(), terms, conditions.temperature(), conditions.pressure(), recordSets);
        return new BuildResult(request, missingCompounds, missingPropertyTypes, missingPhaseSpecificRecords,
                missingPhysicalStates, unsupportedStates);
    }

    private Optional<ReactionThermodynamicRecordSet> recordSet(String compoundCode, MatterState state,
                                                               ThermodynamicReferenceConditions requestConditions) {
        EnumMap<ThermodynamicPropertyType, ThermodynamicPropertyDetails> details = new EnumMap<>(ThermodynamicPropertyType.class);
        for (ThermodynamicPropertyType type : ThermodynamicPropertyType.values()) {
            Optional<ThermodynamicPropertyDetails> exact = thermodynamicReferenceService.findExact(compoundCode, type, state,
                    requestConditions.temperature(), requestConditions.pressure());
            if (exact.isEmpty()) {
                return Optional.empty();
            }
            details.put(type, exact.orElseThrow());
        }

        ThermodynamicPropertyDetails enthalpy = details.get(ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION);
        ThermodynamicReferenceConditions recordConditions = new ThermodynamicReferenceConditions(
                requestConditions.temperature(), requestConditions.pressure(), state,
                enthalpy.conditions().standardStateConvention());

        return Optional.of(new ReactionThermodynamicRecordSet(compoundCode, state, recordConditions,
                source(enthalpy),
                source(details.get(ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION)),
                source(details.get(ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY)),
                source(details.get(ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY))));
    }

    private ReactionThermodynamicsResult incomplete(ReactionDetails reaction, BuildResult build) {
        ReactionThermodynamicCoverage coverage = ReactionThermodynamicCoverage.incomplete(reaction.reactionCode(),
                build.missingCompounds().stream().distinct().sorted().toList(),
                build.missingPropertyTypes().stream().distinct().sorted().toList(),
                build.missingPhaseSpecificRecords().stream().distinct().sorted().toList(),
                build.missingPhysicalStates().stream().distinct().sorted().toList(),
                build.unsupportedStates().stream().distinct().sorted().toList());
        return new ReactionThermodynamicsResult(reaction.reactionCode(), reaction.canonicalEquation(),
                ReactionThermodynamicStatus.INCOMPLETE_COVERAGE, coverage, Map.of(), List.of(),
                ReactionThermodynamicVector.of(List.of()), ThermodynamicCalculationMethod.FORMATION_SUM,
                "Incomplete thermodynamic coverage; no missing value was defaulted to zero.");
    }

    private boolean profileExists(String compoundCode) {
        try {
            thermodynamicReferenceService.getProfile(compoundCode);
            return true;
        } catch (ThermodynamicException ex) {
            if (ex.getErrorCode() == ThermodynamicErrorCode.MISSING_PROFILE) {
                return false;
            }
            throw ex;
        }
    }

    private static ReactionThermodynamicSourceProperty source(ThermodynamicPropertyDetails details) {
        return new ReactionThermodynamicSourceProperty(details.type(), details.value(), details.unitSymbol(),
                new ThermodynamicProvenance(details.provenance().sourceIdentifier(), details.provenance().citation(),
                        details.provenance().reuseLimitations()));
    }

    private static ReactionThermodynamicRequestTerm requestTerm(ReactionDetails.TermDetails term, ReactionSpeciesState state) {
        return new ReactionThermodynamicRequestTerm(term.compoundCode(), term.formula(), term.side(), term.coefficient(), state);
    }

    private static MatterState resolveState(ReactionDetails.TermDetails term, Map<String, MatterState> overrides) {
        MatterState override = overrides.getOrDefault(term.compoundCode() + "|" + term.termOrder(), overrides.get(term.compoundCode()));
        if (override != null) {
            return override;
        }
        return switch (term.speciesState()) {
            case GAS -> MatterState.GAS;
            case LIQUID -> MatterState.LIQUID;
            case SOLID -> MatterState.SOLID;
            case UNKNOWN -> MatterState.UNKNOWN;
            case AQUEOUS, DISSOLVED, MOLTEN -> null;
        };
    }

    private static ReactionSpeciesState toReactionSpeciesState(MatterState state) {
        return switch (state) {
            case GAS -> ReactionSpeciesState.GAS;
            case LIQUID -> ReactionSpeciesState.LIQUID;
            case SOLID -> ReactionSpeciesState.SOLID;
            default -> ReactionSpeciesState.UNKNOWN;
        };
    }

    private static String key(String compoundCode, MatterState state) {
        return compoundCode + "|" + state.name();
    }

    private record BuildResult(
            ReactionThermodynamicsRequest request,
            List<String> missingCompounds,
            List<String> missingPropertyTypes,
            List<String> missingPhaseSpecificRecords,
            List<String> missingPhysicalStates,
            List<String> unsupportedStates) {

        private boolean hasMissingCoverage() {
            return !missingCompounds.isEmpty()
                    || !missingPropertyTypes.isEmpty()
                    || !missingPhaseSpecificRecords.isEmpty()
                    || !missingPhysicalStates.isEmpty()
                    || !unsupportedStates.isEmpty();
        }
    }
}

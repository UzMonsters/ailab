package com.ailab.chemistry.service;

import com.ailab.chemistry.api.EquilibriumCompositionService;
import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ThermodynamicEquilibriumService;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityCorrectionResult;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.reaction.ReactionErrorCode;
import com.ailab.chemistry.domain.reaction.ReactionException;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCalculationStatus;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionCalculator;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionErrorCode;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionException;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionMethod;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionRequest;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionResult;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionStatus;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumConstantResult;
import com.ailab.chemistry.domain.thermodynamics.InitialParticipantAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class EquilibriumCompositionServiceImpl implements EquilibriumCompositionService {

    private static final Pressure DEFAULT_STANDARD_PRESSURE = Pressure.of("1.000", PressureUnit.BAR);

    private final ReactionCatalogService reactionCatalogService;
    private final ThermodynamicEquilibriumService thermodynamicEquilibriumService;
    private final IonicActivityService ionicActivityService;
    private final EquilibriumCompositionCalculator calculator = new EquilibriumCompositionCalculator();

    @Autowired
    public EquilibriumCompositionServiceImpl(
            ReactionCatalogService reactionCatalogService,
            ThermodynamicEquilibriumService thermodynamicEquilibriumService,
            @Autowired(required = false) IonicActivityService ionicActivityService) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.thermodynamicEquilibriumService = Objects.requireNonNull(thermodynamicEquilibriumService, "thermodynamicEquilibriumService must not be null");
        this.ionicActivityService = ionicActivityService;
    }

    @Override
    public EquilibriumCompositionResult calculate(EquilibriumCompositionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // 1. Validate reaction exists
        try {
            reactionCatalogService.getByCode(request.reactionCode());
        } catch (ReactionException ex) {
            if (ex.getErrorCode() == ReactionErrorCode.REACTION_NOT_FOUND) {
                throw new EquilibriumCompositionException(
                        EquilibriumCompositionErrorCode.UNKNOWN_REACTION,
                        "Unknown reaction code: " + request.reactionCode());
            }
            throw ex;
        }

        // 2. Fetch standard thermodynamic equilibrium constant K(T)
        Pressure stdPress = request.standardPressure() != null ? request.standardPressure() : DEFAULT_STANDARD_PRESSURE;
        EquilibriumConstantResult constantResult = thermodynamicEquilibriumService.calculateStandardConstant(
                request.reactionCode(), request.temperature(), stdPress, request.stateOverrides());

        if (constantResult.status() != EquilibriumCalculationStatus.CALCULABLE || constantResult.standardConstant() == null) {
            return new EquilibriumCompositionResult(
                    request.reactionCode(),
                    request.temperature(),
                    EquilibriumCompositionStatus.INCOMPLETE_COVERAGE,
                    null,
                    List.of(),
                    null,
                    null,
                    null,
                    constantResult.coverage(),
                    constantResult.phaseStabilityStatus(),
                    request.method(),
                    "Standard thermodynamic reaction value is unavailable for " + request.reactionCode() + "; equilibrium composition was not calculated.",
                    List.of("Incomplete thermodynamic coverage")
            );
        }

        // 3. Prepare Davies activity provider if model is AQUEOUS_DAVIES
        EquilibriumCompositionCalculator.DaviesActivityProvider daviesProvider = null;
        if (request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
            if (ionicActivityService == null) {
                throw new EquilibriumCompositionException(
                        EquilibriumCompositionErrorCode.INCOMPLETE_THERMODYNAMIC_COVERAGE,
                        "IonicActivityService is unavailable for AQUEOUS_DAVIES calculation");
            }
            daviesProvider = (activeSpecies, spectatorIons, volumeLiters, temp) -> {
                List<IonicSpeciesConcentration> speciesList = new ArrayList<>();
                for (InitialParticipantAmount amt : activeSpecies) {
                    if (amt.moles().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal conc = amt.moles().divide(volumeLiters, com.ailab.chemistry.domain.measurement.ScientificMath.CALCULATION_CONTEXT);
                        String specCode = amt.speciesCode() != null ? amt.speciesCode() : amt.compoundCode();
                        int charge = amt.ionicCharge() != null ? amt.ionicCharge() : 0;
                        speciesList.add(new IonicSpeciesConcentration(specCode, conc, charge));
                    }
                }
                for (InitialParticipantAmount spec : spectatorIons) {
                    if (spec.moles().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal conc = spec.moles().divide(volumeLiters, com.ailab.chemistry.domain.measurement.ScientificMath.CALCULATION_CONTEXT);
                        String specCode = spec.speciesCode() != null ? spec.speciesCode() : spec.compoundCode();
                        int charge = spec.ionicCharge() != null ? spec.ionicCharge() : 0;
                        speciesList.add(new IonicSpeciesConcentration(specCode, conc, charge));
                    }
                }

                try {
                    ActivityCorrectionResult res = ionicActivityService.calculateActivities(speciesList, temp, "COMP-H2O", ActivityModel.DAVIES);
                    return res.coefficientMap();
                } catch (ActivityException ex) {
                    throw new EquilibriumCompositionException(
                            EquilibriumCompositionErrorCode.DAVIES_VALIDITY_EXCEEDED,
                            "Davies validity range exceeded or ionic activity calculation failed: " + ex.getMessage());
                }
            };
        }

        // 4. Solve using pure EquilibriumCompositionCalculator
        return calculator.solve(
                constantResult.reactionVector(),
                constantResult.standardConstant(),
                request,
                constantResult.coverage(),
                daviesProvider
        );
    }
}

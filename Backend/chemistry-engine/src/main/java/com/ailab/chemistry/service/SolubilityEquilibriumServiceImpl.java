package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.IonicActivityService;
import com.ailab.chemistry.api.SolubilityEquilibriumService;
import com.ailab.chemistry.domain.acidbase.AcidBaseException;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSetRepository;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.solubility.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SolubilityEquilibriumServiceImpl implements SolubilityEquilibriumService {
    private final SolubilityReferenceRepository referenceRepository;
    private final IonicActivityService ionicActivityService;
    private final CompoundCatalogService compoundCatalogService;
    private final ActivityParameterSetRepository activityParameterSetRepository;
    private final SolubilityEquilibriumCalculator calculator = new SolubilityEquilibriumCalculator();

    @Autowired
    public SolubilityEquilibriumServiceImpl(
            SolubilityReferenceRepository referenceRepository,
            IonicActivityService ionicActivityService,
            CompoundCatalogService compoundCatalogService,
            ActivityParameterSetRepository activityParameterSetRepository) {
        this.referenceRepository = Objects.requireNonNull(referenceRepository, "referenceRepository must not be null");
        this.ionicActivityService = Objects.requireNonNull(ionicActivityService, "ionicActivityService must not be null");
        this.compoundCatalogService = Objects.requireNonNull(compoundCatalogService, "compoundCatalogService must not be null");
        this.activityParameterSetRepository = Objects.requireNonNull(activityParameterSetRepository, "activityParameterSetRepository must not be null");
    }

    public SolubilityEquilibriumServiceImpl(
            SolubilityReferenceRepository referenceRepository,
            IonicActivityService ionicActivityService,
            CompoundCatalogService compoundCatalogService) {
        this(referenceRepository, ionicActivityService, compoundCatalogService, new com.ailab.chemistry.infrastructure.persistence.acidbase.InMemoryActivityParameterSetRepository());
    }

    @Override
    public SaturationResult calculateSaturation(SaturationRequest request) {
        SolubilityEquilibrium equilibrium = resolve(request.equilibrium(), request.equilibriumCode(), request.temperature(), request.solventCode());
        ActivityParameterSet parameterSet = parameterSet(request.activityModel(), request.temperature(), request.solventCode());
        validateSpecies(equilibrium, request.dissolvedIons());
        assertActivitySupported(parameterSet, combine(request.dissolvedIons(), request.spectatorIons()), request.temperature(), request.solventCode());
        return calculator.calculateSaturation(new SaturationRequest(equilibrium, request.dissolvedIons(), request.spectatorIons(), parameterSet, request.comparisonTolerance()));
    }

    @Override
    public MolarSolubilityResult calculateMolarSolubility(MolarSolubilityRequest request) {
        ActivityParameterSet parameterSet = parameterSet(request.activityParameterSet().model(),
                request.equilibrium().conditions().temperature(), request.equilibrium().conditions().solventCode());
        assertActivitySupported(parameterSet, combine(request.initialIons(), request.spectatorIons()),
                request.equilibrium().conditions().temperature(), request.equilibrium().conditions().solventCode());
        return calculator.calculateMolarSolubility(new MolarSolubilityRequest(request.equilibrium(), request.initialIons(), request.spectatorIons(), parameterSet, request.comparisonTolerance()));
    }

    @Override
    public PrecipitationResult calculatePrecipitation(PrecipitationRequest request) {
        SolubilityEquilibrium equilibrium = resolve(request.equilibrium(), request.equilibriumCode(), request.temperature(), request.solventCode());
        ActivityParameterSet parameterSet = parameterSet(request.activityModel(), request.temperature(), request.solventCode());
        BigDecimal molarMass = request.precipitateMolarMassGramsPerMole() == null
                ? compoundCatalogService.getByCode(equilibrium.solidCompoundCode()).getMolarMassValue()
                : request.precipitateMolarMassGramsPerMole();
        return calculator.calculatePrecipitation(new PrecipitationRequest(equilibrium, request.ionAmounts(), request.finalVolume(),
                request.spectatorIons(), parameterSet, molarMass, request.comparisonTolerance()));
    }

    private SolubilityEquilibrium resolve(SolubilityEquilibrium existing, String code, Temperature temperature, String solventCode) {
        if (existing != null) {
            return existing;
        }
        return referenceRepository.findByCode(new SolubilityEquilibriumCode(code), temperature, solventCode)
                .orElseThrow(() -> {
                    boolean knownCode = referenceRepository.findAll().stream().anyMatch(eq -> eq.code().value().equalsIgnoreCase(code));
                    return new SolubilityException(knownCode ? SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS : SolubilityErrorCode.MISSING_KSP,
                            knownCode ? "Unsupported solubility reference conditions for " + code : "Missing solubility product for " + code);
                });
    }

    private ActivityParameterSet parameterSet(ActivityModel model, Temperature temperature, String solventCode) {
        if (temperature == null || solventCode == null) {
            throw new SolubilityException(SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS, "Temperature and solvent are required");
        }
        return activityParameterSetRepository.findBy(model, temperature, solventCode)
                .orElseThrow(() -> new SolubilityException(SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS,
                        "Missing activity parameter set for " + model + " at " + temperature.in(TemperatureUnit.CELSIUS).setScale(2, RoundingMode.HALF_UP)));
    }

    private void assertActivitySupported(ActivityParameterSet parameterSet, List<IonicSpeciesConcentration> ions, Temperature temperature, String solventCode) {
        try {
            ionicActivityService.calculateActivities(ions, temperature, solventCode, parameterSet.model());
        } catch (ActivityException ex) {
            if (ex.getErrorCode().name().contains("OUTSIDE")) {
                throw new SolubilityException(SolubilityErrorCode.OUTSIDE_ACTIVITY_MODEL_RANGE, ex.getMessage());
            }
            throw new SolubilityException(SolubilityErrorCode.UNSUPPORTED_REFERENCE_CONDITIONS, ex.getMessage());
        } catch (AcidBaseException ex) {
            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, ex.getMessage());
        }
    }

    private void validateSpecies(SolubilityEquilibrium equilibrium, List<IonicSpeciesConcentration> ions) {
        for (IonicSpeciesConcentration ion : ions) {
            equilibrium.terms().stream()
                    .filter(term -> term.speciesCode().equalsIgnoreCase(ion.speciesCode()))
                    .findFirst()
                    .ifPresent(term -> {
                        if (term.charge() != ion.charge()) {
                            throw new SolubilityException(SolubilityErrorCode.INVALID_ION_SPECIES, "Ion charge does not match solubility reference");
                        }
                    });
        }
    }

    private List<IonicSpeciesConcentration> combine(List<IonicSpeciesConcentration> first, List<IonicSpeciesConcentration> second) {
        java.util.ArrayList<IonicSpeciesConcentration> ions = new java.util.ArrayList<>(first == null ? List.of() : first);
        ions.addAll(second == null ? List.of() : second);
        return ions;
    }
}

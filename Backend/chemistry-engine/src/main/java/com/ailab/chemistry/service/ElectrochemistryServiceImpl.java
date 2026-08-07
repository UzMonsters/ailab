package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.ElectrochemistryService;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellRequest;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalCellResult;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceRepository;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemistryCalculator;
import com.ailab.chemistry.domain.electrochemistry.ElectrolysisRequest;
import com.ailab.chemistry.domain.electrochemistry.ElectrolysisResult;
import com.ailab.chemistry.domain.electrochemistry.NernstRequest;
import com.ailab.chemistry.domain.electrochemistry.NernstResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ElectrochemistryServiceImpl implements ElectrochemistryService {
    private final ElectrochemicalReferenceRepository repository;
    private final CompoundCatalogService compoundCatalogService;
    private final ElectrochemistryCalculator calculator = new ElectrochemistryCalculator();

    public ElectrochemistryServiceImpl(ElectrochemicalReferenceRepository repository, CompoundCatalogService compoundCatalogService) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.compoundCatalogService = Objects.requireNonNull(compoundCatalogService, "compoundCatalogService must not be null");
    }

    @Override
    public ElectrochemicalCellResult calculateStandardCell(ElectrochemicalCellRequest request) {
        return calculator.calculateStandardCell(request, repository);
    }

    @Override
    public NernstResult calculateNonstandardCell(NernstRequest request) {
        return calculator.calculateNonstandardCell(request, repository);
    }

    @Override
    public ElectrolysisResult calculateElectrolysis(ElectrolysisRequest request) {
        if (request.molarMassGramsPerMole() != null) {
            return calculator.calculateElectrolysis(request, repository);
        }
        var details = compoundCatalogService.getByCode(request.substanceCode());
        ElectrolysisRequest enriched = new ElectrolysisRequest(
                request.halfReactionRecordId(),
                request.substanceCode(),
                request.substancePhase(),
                request.current(),
                request.duration(),
                request.charge(),
                request.efficiency(),
                details.getMolarMassValue()
        );
        return calculator.calculateElectrolysis(enriched, repository);
    }
}

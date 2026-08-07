package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundCode;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.CompoundRepository;
import com.ailab.chemistry.domain.physicalproperty.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompoundPhysicalPropertyServiceImpl implements CompoundPhysicalPropertyService {

    private final CompoundPhysicalPropertyProfileRepository propertyRepository;
    private final CompoundRepository compoundRepository;

    public CompoundPhysicalPropertyServiceImpl(CompoundPhysicalPropertyProfileRepository propertyRepository, CompoundRepository compoundRepository) {
        this.propertyRepository = propertyRepository;
        this.compoundRepository = compoundRepository;
    }

    @Override
    public CompoundPhysicalPropertyDetails getByCompoundId(UUID compoundId) {
        CompoundPhysicalPropertyProfile profile = propertyRepository.findByCompoundId(new CompoundId(compoundId))
                .orElseThrow(() -> new CompoundPhysicalPropertyException(
                        CompoundPhysicalPropertyErrorCode.PHYSICAL_PROPERTY_PROFILE_NOT_FOUND,
                        "Physical property profile not found for compound ID: " + compoundId));

        Compound compound = compoundRepository.findById(new CompoundId(compoundId)).orElse(null);
        String code = compound != null ? compound.getCode().getValue() : compoundId.toString();
        String name = compound != null ? compound.getPrimaryName() : "Unknown Compound";

        return toDetails(profile, code, name);
    }

    @Override
    public CompoundPhysicalPropertyDetails getByCompoundCode(String compoundCode) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new CompoundPhysicalPropertyException(
                        CompoundPhysicalPropertyErrorCode.PHYSICAL_PROPERTY_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        CompoundPhysicalPropertyProfile profile = propertyRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new CompoundPhysicalPropertyException(
                        CompoundPhysicalPropertyErrorCode.PHYSICAL_PROPERTY_PROFILE_NOT_FOUND,
                        "Physical property profile not found for compound code: " + compoundCode));

        return toDetails(profile, compound.getCode().getValue(), compound.getPrimaryName());
    }

    @Override
    public List<CompoundSummary> findWithAvailableProperty(PhysicalPropertyType propertyType) {
        List<CompoundPhysicalPropertyProfile> profiles = propertyRepository.findWithAvailableProperty(propertyType);
        List<CompoundSummary> summaries = new ArrayList<>();
        for (CompoundPhysicalPropertyProfile p : profiles) {
            compoundRepository.findById(p.getCompoundId()).ifPresent(c -> summaries.add(toSummary(c)));
        }
        return summaries;
    }

    private CompoundPhysicalPropertyDetails toDetails(CompoundPhysicalPropertyProfile profile, String code, String name) {
        Map<String, String> availMap = profile.getAvailabilityMap().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue().name()));

        return new CompoundPhysicalPropertyDetails(
                profile.getCompoundId().getValue(),
                code,
                name,
                profile.getDatasetVersion(),
                availMap
        );
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
}

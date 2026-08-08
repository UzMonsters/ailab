package com.ailab.chemistry.infrastructure.persistence.hazard;

import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;
import com.ailab.chemistry.domain.hazard.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryHazardProfileRepository implements HazardProfileRepository {

    private final List<HazardProfile> profiles;
    private final ElementMassProvider massProvider;

    public InMemoryHazardProfileRepository(ElementMassProvider massProvider) {
        this.massProvider = massProvider;
        this.profiles = new ArrayList<>(KnownHazardRegistry.buildAll55Profiles(massProvider));
    }

    @Override
    public Optional<HazardProfile> findByCompoundId(CompoundId compoundId) {
        return profiles.stream().filter(p -> p.getCompoundId().equals(compoundId)).findFirst();
    }

    @Override
    public Optional<HazardProfile> findByCompoundCode(String compoundCode) {
        return profiles.stream()
                .filter(p -> p.getCompoundId().equals(KnownCompoundRegistry.buildAll55CoreCompounds(massProvider).stream()
                        .filter(c -> c.getCode().getValue().equalsIgnoreCase(compoundCode))
                        .map(Compound::getId)
                        .findFirst()
                        .orElse(null)))
                .findFirst();
    }

    @Override
    public List<HazardProfile> findBySummaryFlag(HazardSummaryFlag flag) {
        return profiles.stream()
                .filter(p -> p.getSummaryFlags().contains(flag))
                .collect(Collectors.toList());
    }

    @Override
    public List<HazardProfile> findByPictogramCode(String pictogramCode) {
        return profiles.stream()
                .filter(p -> p.getClassifications().stream().anyMatch(cl -> cl.getPictograms().stream().anyMatch(pic -> pic.getCode().equalsIgnoreCase(pictogramCode))))
                .collect(Collectors.toList());
    }

    @Override
    public List<HazardProfile> findByHazardClass(String classificationSystem, String hazardClassCode) {
        return profiles.stream()
                .filter(p -> p.getClassifications().stream().anyMatch(cl ->
                        cl.getClassificationSystem().name().equalsIgnoreCase(classificationSystem) &&
                        cl.getHazardClassCode().equalsIgnoreCase(hazardClassCode)))
                .collect(Collectors.toList());
    }

    @Override
    public List<HazardProfile> findAll() {
        return new ArrayList<>(profiles);
    }

    @Override
    public long count() {
        return profiles.size();
    }
}

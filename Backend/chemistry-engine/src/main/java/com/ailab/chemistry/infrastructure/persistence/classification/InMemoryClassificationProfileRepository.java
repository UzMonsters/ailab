package com.ailab.chemistry.infrastructure.persistence.classification;

import com.ailab.chemistry.domain.classification.*;
import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;
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
public class InMemoryClassificationProfileRepository implements ClassificationProfileRepository {

    private final List<ClassificationProfile> profiles;
    private final ElementMassProvider massProvider;

    public InMemoryClassificationProfileRepository(ElementMassProvider massProvider) {
        this.massProvider = massProvider;
        this.profiles = new ArrayList<>(KnownClassificationRegistry.buildAll55Profiles(massProvider));
    }

    @Override
    public Optional<ClassificationProfile> findByCompoundId(CompoundId compoundId) {
        return profiles.stream().filter(p -> p.getCompoundId().equals(compoundId)).findFirst();
    }

    @Override
    public Optional<ClassificationProfile> findByCompoundCode(String compoundCode) {
        return profiles.stream()
                .filter(p -> p.getCompoundId().equals(KnownCompoundRegistry.buildAll55CoreCompounds(massProvider).stream()
                        .filter(c -> c.getCode().getValue().equalsIgnoreCase(compoundCode))
                        .map(Compound::getId)
                        .findFirst()
                        .orElse(null)))
                .findFirst();
    }

    @Override
    public List<ClassificationProfile> findByClassificationCode(ClassificationCode classificationCode) {
        return profiles.stream()
                .filter(p -> p.hasClassification(classificationCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassificationProfile> findAll() {
        return new ArrayList<>(profiles);
    }

    @Override
    public long count() {
        return profiles.size();
    }

    @Override
    public ClassificationTaxonomy getActiveTaxonomy() {
        return KnownClassificationRegistry.TAXONOMY;
    }
}

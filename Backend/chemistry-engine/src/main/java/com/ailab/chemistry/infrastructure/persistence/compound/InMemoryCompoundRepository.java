package com.ailab.chemistry.infrastructure.persistence.compound;

import com.ailab.chemistry.domain.compound.*;
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
public class InMemoryCompoundRepository implements CompoundRepository {

    private final List<Compound> compounds;

    public InMemoryCompoundRepository(ElementMassProvider massProvider) {
        this.compounds = new ArrayList<>(KnownCompoundRegistry.buildAll55CoreCompounds(massProvider));
    }

    @Override
    public Optional<Compound> findById(CompoundId id) {
        return compounds.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Compound> findByCode(CompoundCode code) {
        return compounds.stream().filter(c -> c.getCode().equals(code)).findFirst();
    }

    @Override
    public List<Compound> findByNormalizedFormula(String normalizedFormula) {
        return compounds.stream()
                .filter(c -> c.getFormula().getNormalizedFormula().equalsIgnoreCase(normalizedFormula))
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> findByCompositionFormula(String compositionFormula) {
        return compounds.stream()
                .filter(c -> c.getFormula().getCompositionFormula().equalsIgnoreCase(compositionFormula))
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> searchByName(String query) {
        if (query == null || query.isBlank()) return List.of();
        String lower = query.trim().toLowerCase();
        return compounds.stream()
                .filter(c -> c.getPrimaryName().toLowerCase().contains(lower) ||
                             c.getCode().getValue().toLowerCase().contains(lower) ||
                             c.getAliases().stream().anyMatch(a -> a.getName().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> findAll() {
        return new ArrayList<>(compounds);
    }

    @Override
    public long count() {
        return compounds.size();
    }

    @Override
    public Compound save(Compound compound) {
        compounds.add(compound);
        return compound;
    }
}

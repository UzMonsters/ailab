package com.ailab.chemistry.infrastructure.persistence.element.property;

import com.ailab.chemistry.domain.element.property.ElementPropertyProfile;
import com.ailab.chemistry.domain.element.property.ElementPropertyRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!(test | standalone-engine)")
public class ElementPropertyRepositoryImpl implements ElementPropertyRepository {

    private final JpaElementPropertyProfileRepository repository;

    public ElementPropertyRepositoryImpl(JpaElementPropertyProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ElementPropertyProfile> findByAtomicNumber(int atomicNumber) {
        return repository.findByAtomicNumber(atomicNumber)
                .map(ElementPropertyMapper::toDomain);
    }

    @Override
    public Optional<ElementPropertyProfile> findBySymbol(String symbol) {
        return repository.findBySymbol(symbol)
                .map(ElementPropertyMapper::toDomain);
    }
}

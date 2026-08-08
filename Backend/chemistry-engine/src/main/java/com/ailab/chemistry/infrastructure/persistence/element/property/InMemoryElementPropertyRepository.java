package com.ailab.chemistry.infrastructure.persistence.element.property;

import com.ailab.chemistry.domain.element.property.ElementPropertyProfile;
import com.ailab.chemistry.domain.element.property.ElementPropertyRepository;
import com.ailab.chemistry.domain.element.property.KnownElementPropertyRegistry;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryElementPropertyRepository implements ElementPropertyRepository {

    private final Map<Integer, ElementPropertyProfile> byAtomicNumber = new ConcurrentHashMap<>();
    private final Map<String, ElementPropertyProfile> bySymbol = new ConcurrentHashMap<>();

    public InMemoryElementPropertyRepository() {
        for (ElementPropertyProfile profile : KnownElementPropertyRegistry.buildAll118Profiles()) {
            byAtomicNumber.put(profile.getAtomicNumber(), profile);
            bySymbol.put(profile.getSymbol(), profile);
        }
    }

    @Override
    public Optional<ElementPropertyProfile> findByAtomicNumber(int atomicNumber) {
        return Optional.ofNullable(byAtomicNumber.get(atomicNumber));
    }

    @Override
    public Optional<ElementPropertyProfile> findBySymbol(String symbol) {
        return Optional.ofNullable(bySymbol.get(symbol));
    }
}

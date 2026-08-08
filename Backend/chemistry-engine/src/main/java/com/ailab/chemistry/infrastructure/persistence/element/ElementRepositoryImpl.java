package com.ailab.chemistry.infrastructure.persistence.element;

import com.ailab.chemistry.domain.element.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("!(test | standalone-engine)")
public class ElementRepositoryImpl implements ElementRepository {
    private final JpaElementRepository jpaRepository;

    public ElementRepositoryImpl(JpaElementRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Element> findByAtomicNumber(int atomicNumber) {
        return jpaRepository.findByAtomicNumber(atomicNumber).map(ElementMapper::toDomain);
    }

    @Override
    public Optional<Element> findBySymbol(String symbol) {
        return jpaRepository.findBySymbol(symbol).map(ElementMapper::toDomain);
    }

    @Override
    public List<Element> findAll() {
        return jpaRepository.findAll().stream()
                .map(ElementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Element element) {
        jpaRepository.save(ElementMapper.toEntity(element));
    }

    @Override
    public void saveAll(List<Element> elements) {
        List<ElementEntity> entities = elements.stream()
                .map(ElementMapper::toEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }
}

package com.ailab.chemistry.infrastructure.persistence.reaction;

import com.ailab.chemistry.domain.reaction.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JpaReactionRepositoryAdapter implements ReactionRepository {

    private final SpringDataJpaReactionRepository springRepository;
    private final ReactionEntityMapper mapper;

    public JpaReactionRepositoryAdapter(SpringDataJpaReactionRepository springRepository) {
        this.springRepository = Objects.requireNonNull(springRepository, "SpringDataJpaReactionRepository must not be null");
        this.mapper = new ReactionEntityMapper();
    }

    @Override
    public Optional<Reaction> findById(ReactionId id) {
        return springRepository.findById(id.getValue()).map(mapper::toDomain);
    }

    @Override
    public Optional<Reaction> findByCode(ReactionCode code) {
        return springRepository.findByReactionCode(code.getValue()).map(mapper::toDomain);
    }

    @Override
    public List<Reaction> findByReactantCompoundCode(String compoundCode) {
        return springRepository.findByReactantCompoundCode(compoundCode)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByProductCompoundCode(String compoundCode) {
        return springRepository.findByProductCompoundCode(compoundCode)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findInvolvingCompoundCode(String compoundCode) {
        return springRepository.findInvolvingCompoundCode(compoundCode)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByReactionTypeCode(ReactionTypeCode typeCode) {
        return springRepository.findByReactionTypeCode(typeCode.name())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findReversible() {
        return springRepository.findByDirectionality(ReactionDirectionality.REVERSIBLE.name())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findAll() {
        return springRepository.findAll()
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Reaction save(Reaction reaction) {
        ReactionEntity entity = mapper.toEntity(reaction);
        ReactionEntity saved = springRepository.save(entity);
        return mapper.toDomain(saved);
    }
}

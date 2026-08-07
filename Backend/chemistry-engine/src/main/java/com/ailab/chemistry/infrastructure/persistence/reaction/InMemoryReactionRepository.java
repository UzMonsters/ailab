package com.ailab.chemistry.infrastructure.persistence.reaction;

import com.ailab.chemistry.domain.reaction.*;
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
public class InMemoryReactionRepository implements ReactionRepository {

    private final List<Reaction> reactions;

    public InMemoryReactionRepository() {
        this.reactions = new ArrayList<>(KnownReactionRegistry.buildAll26Reactions());
    }

    @Override
    public Optional<Reaction> findById(ReactionId id) {
        return reactions.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Reaction> findByCode(ReactionCode code) {
        return reactions.stream().filter(r -> r.getReactionCode().equals(code)).findFirst();
    }

    @Override
    public List<Reaction> findByReactantCompoundCode(String compoundCode) {
        return reactions.stream()
                .filter(r -> r.getReactants().stream().anyMatch(t -> t.getCompoundCode().equalsIgnoreCase(compoundCode)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByProductCompoundCode(String compoundCode) {
        return reactions.stream()
                .filter(r -> r.getProducts().stream().anyMatch(t -> t.getCompoundCode().equalsIgnoreCase(compoundCode)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findInvolvingCompoundCode(String compoundCode) {
        return reactions.stream()
                .filter(r -> r.getTerms().stream().anyMatch(t -> t.getCompoundCode().equalsIgnoreCase(compoundCode)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findByReactionTypeCode(ReactionTypeCode typeCode) {
        return reactions.stream()
                .filter(r -> r.getTypeAssignments().stream().anyMatch(ta -> ta.getTypeCode() == typeCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findReversible() {
        return reactions.stream()
                .filter(r -> r.getDirectionality() == ReactionDirectionality.REVERSIBLE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reaction> findAll() {
        return List.copyOf(reactions);
    }

    @Override
    public Reaction save(Reaction reaction) {
        reactions.removeIf(r -> r.getId().equals(reaction.getId()));
        reactions.add(reaction);
        return reaction;
    }
}

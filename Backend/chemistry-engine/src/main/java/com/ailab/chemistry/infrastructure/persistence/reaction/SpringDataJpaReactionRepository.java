package com.ailab.chemistry.infrastructure.persistence.reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaReactionRepository extends JpaRepository<ReactionEntity, UUID> {

    Optional<ReactionEntity> findByReactionCode(String reactionCode);

    @Query("SELECT DISTINCT r FROM ReactionEntity r JOIN r.terms t WHERE t.side = 'REACTANT' AND LOWER(t.compoundCode) = LOWER(:compoundCode)")
    List<ReactionEntity> findByReactantCompoundCode(@Param("compoundCode") String compoundCode);

    @Query("SELECT DISTINCT r FROM ReactionEntity r JOIN r.terms t WHERE t.side = 'PRODUCT' AND LOWER(t.compoundCode) = LOWER(:compoundCode)")
    List<ReactionEntity> findByProductCompoundCode(@Param("compoundCode") String compoundCode);

    @Query("SELECT DISTINCT r FROM ReactionEntity r JOIN r.terms t WHERE LOWER(t.compoundCode) = LOWER(:compoundCode)")
    List<ReactionEntity> findInvolvingCompoundCode(@Param("compoundCode") String compoundCode);

    @Query("SELECT DISTINCT r FROM ReactionEntity r JOIN r.typeAssignments ta WHERE ta.typeCode = :typeCode")
    List<ReactionEntity> findByReactionTypeCode(@Param("typeCode") String typeCode);

    List<ReactionEntity> findByDirectionality(String directionality);
}

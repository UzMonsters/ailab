package com.ailab.auth.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    List<EmailVerificationToken> findAllByUserIdAndPurposeAndConsumedAtIsNull(String userId, String purpose);

    Optional<EmailVerificationToken> findTopByUserIdAndPurposeOrderByCreatedAtDesc(String userId, String purpose);

    long countByUserIdAndPurposeAndCreatedAtAfter(String userId, String purpose, Instant after);

    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.consumedAt = :now WHERE t.id = :id AND t.consumedAt IS NULL AND t.expiresAt > :now")
    int consumeAtomically(@Param("id") String id, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE EmailVerificationToken t SET t.consumedAt = :now WHERE t.userId = :userId AND t.purpose = :purpose AND t.consumedAt IS NULL")
    int invalidateAllForUserAndPurpose(@Param("userId") String userId, @Param("purpose") String purpose, @Param("now") Instant now);
}
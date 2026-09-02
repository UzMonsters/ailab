package com.ailab.auth.token;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken t set t.revokedAt = CURRENT_INSTANT where t.userId = :userId and t.revokedAt is null")
    int revokeAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("update RefreshToken t set t.revokedAt = CURRENT_INSTANT where t.familyId = :familyId and t.revokedAt is null")
    int revokeAllByFamilyId(@Param("familyId") String familyId);

    Page<RefreshToken> findAllByUserIdAndRevokedAtIsNullAndExpiresAtAfter(String userId, Instant now, Pageable pageable);

    List<RefreshToken> findAllByUserIdAndRevokedAtIsNullAndExpiresAtAfter(String userId, Instant now, Sort sort);

    long countByUserIdAndRevokedAtIsNullAndExpiresAtAfter(String userId, Instant now);

    Optional<RefreshToken> findByIdAndUserId(String id, String userId);

    @Modifying
    @Query("update RefreshToken t set t.revokedAt = CURRENT_INSTANT where t.id = :id and t.userId = :userId and t.revokedAt is null")
    int revokeByIdAndUserId(@Param("id") String id, @Param("userId") String userId);
}

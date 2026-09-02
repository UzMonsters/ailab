package com.ailab.auth.token;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
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
}

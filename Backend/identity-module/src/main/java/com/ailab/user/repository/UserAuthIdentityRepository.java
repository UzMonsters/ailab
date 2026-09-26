package com.ailab.user.repository;

import com.ailab.user.domain.UserAuthIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAuthIdentityRepository extends JpaRepository<UserAuthIdentity, String> {
    Optional<UserAuthIdentity> findByProviderAndProviderSubject(String provider, String providerSubject);
    boolean existsByProviderAndProviderSubject(String provider, String providerSubject);
    List<UserAuthIdentity> findAllByUserId(String userId);
    Optional<UserAuthIdentity> findByUserIdAndProvider(String userId, String provider);
    void deleteAllByUserIdAndProvider(String userId, String provider);
}
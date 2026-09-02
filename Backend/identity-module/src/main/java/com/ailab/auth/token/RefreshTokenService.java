package com.ailab.auth.token;

import com.ailab.user.api.UserDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService implements RefreshTokenOperations {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final RefreshTokenRepository repository;
    @Value("${app.security.refresh-token-ttl}") private Duration ttl;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IssuedToken issue(String userId) {
        return create(userId, UUID.randomUUID().toString());
    }

    @Transactional(noRollbackFor = RefreshTokenReuseException.class)
    public IssuedToken rotate(String rawToken) {
        RefreshToken current = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token"));
        if (current.isRevoked()) {
            repository.revokeAllByFamilyId(current.getFamilyId());
            throw new RefreshTokenReuseException("Refresh token reuse detected");
        }
        if (current.isExpired(Instant.now())) throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        IssuedToken replacement = create(current.getUserId(), current.getFamilyId());
        current.revoke(hash(replacement.rawToken()));
        repository.save(current);
        return replacement;
    }

    @Transactional
    public Optional<String> revoke(String rawToken) {
        return repository.findByTokenHash(hash(rawToken)).map(token -> {
            if (!token.isRevoked()) token.revoke(null);
            return token.getUserId();
        });
    }

    @Transactional
    public void revokeAll(String userId) {
        repository.revokeAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserDtos.SessionListResponse getActiveSessions(String userId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RefreshToken> tokenPage = repository.findAllByUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, Instant.now(), pageable);
        List<UserDtos.SessionItem> items = tokenPage.getContent().stream()
                .map(t -> new UserDtos.SessionItem(
                        t.getId(),
                        t.getFamilyId(),
                        t.getCreatedAt(),
                        t.getExpiresAt(),
                        false,
                        "Web Browser",
                        "127.0.0.1",
                        t.getCreatedAt()
                ))
                .toList();
        UserDtos.PageMetadata pageMetadata = new UserDtos.PageMetadata(
                tokenPage.getNumber(),
                tokenPage.getSize(),
                tokenPage.getTotalElements(),
                tokenPage.getTotalPages()
        );
        return new UserDtos.SessionListResponse(items, pageMetadata);
    }

    @Transactional
    public void revokeSession(String userId, String sessionId) {
        repository.revokeByIdAndUserId(sessionId, userId);
    }

    private IssuedToken create(String userId, String familyId) {
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes(32));
        repository.save(new RefreshToken(UUID.randomUUID().toString(), userId, familyId, hash(raw), Instant.now().plus(ttl)));
        return new IssuedToken(userId, raw);
    }

    private byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    private String hash(String rawToken) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    public record IssuedToken(String userId, String rawToken) { }
}

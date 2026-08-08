package com.ailab.auth.security;

import com.ailab.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService implements AccessTokenIssuer {
    private final SecretKey key;
    private final Duration ttl;

    public JwtService(@Value("${app.security.jwt-secret}") String secret,
                      @Value("${app.security.access-token-ttl}") Duration ttl) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 256 bits");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttl = ttl;
    }

    @Override
    public String issue(User user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getId()).claim("role", "ROLE_" + user.getRole().name())
                .claim("tokenVersion", user.getTokenVersion())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(ttl))).signWith(key).compact();
    }

    public Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
    @Override
    public long expiresInSeconds() { return ttl.toSeconds(); }
}

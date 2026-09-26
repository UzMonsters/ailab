package com.ailab.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String shareSessionToken = null;
        if (header != null && header.startsWith("ShareSession ")) {
            shareSessionToken = header.substring("ShareSession ".length()).trim();
        } else if (header != null && header.startsWith("Bearer guest_sess_")) {
            shareSessionToken = header.substring("Bearer ".length()).trim();
        } else {
            String queryToken = request.getParameter("sessionToken");
            if (queryToken != null && queryToken.startsWith("guest_sess_")) {
                shareSessionToken = queryToken;
            }
        }

        if (shareSessionToken != null && shareSessionToken.startsWith("guest_sess_")) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("share_session:" + shareSessionToken, null,
                            List.of(new SimpleGrantedAuthority("ROLE_GUEST"))));
        } else if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(header.substring(7));
                String role = claims.get("role", String.class);
                Number tokenVersion = claims.get("tokenVersion", Number.class);
                User user = userRepository.findById(claims.getSubject()).orElse(null);
                if (user == null || tokenVersion == null || user.getTokenVersion() != tokenVersion.longValue()
                        || !role.equals("ROLE_" + user.getRole().name())) {
                    chain.doFilter(request, response);
                    return;
                }

                // Enforce account status
                String status = user.getStatus();
                if ("BLOCKED".equalsIgnoreCase(status) || "DEACTIVATED".equalsIgnoreCase(status) || "DELETION_SCHEDULED".equalsIgnoreCase(status)) {
                    chain.doFilter(request, response);
                    return;
                }
                if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
                    chain.doFilter(request, response);
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                                List.of(new SimpleGrantedAuthority(role))));
            } catch (Exception ignored) { }
        }
        chain.doFilter(request, response);
    }
}
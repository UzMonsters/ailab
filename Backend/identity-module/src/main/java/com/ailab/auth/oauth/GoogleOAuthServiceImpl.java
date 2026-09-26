package com.ailab.auth.oauth;

import com.ailab.auth.security.AuthenticationEligibilityPolicy;
import com.ailab.user.domain.User;
import com.ailab.user.domain.UserAuthIdentity;
import com.ailab.user.repository.UserAuthIdentityRepository;
import com.ailab.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final UserRepository userRepository;
    private final UserAuthIdentityRepository identityRepository;
    private final AuthenticationEligibilityPolicy eligibilityPolicy;

    public GoogleOAuthServiceImpl(UserRepository userRepository,
                                  UserAuthIdentityRepository identityRepository,
                                  AuthenticationEligibilityPolicy eligibilityPolicy) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.eligibilityPolicy = eligibilityPolicy;
    }

    @Override
    public OAuthProcessResult processOAuthLogin(OAuthUserData userData, String linkUserId) {
        if (userData == null || userData.subject() == null || userData.subject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISSING_OAUTH_SUBJECT: OAuth subject identifier is missing");
        }
        if (userData.email() == null || userData.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MISSING_OAUTH_EMAIL: OAuth provider did not supply an email address");
        }
        if (!userData.emailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GOOGLE_EMAIL_NOT_VERIFIED: Your Google email must be verified to sign in");
        }

        String normalizedEmail = userData.email().trim().toLowerCase(Locale.ROOT);
        String provider = userData.provider() != null ? userData.provider() : "google";
        String subject = userData.subject().trim();

        Optional<UserAuthIdentity> existingIdentityOpt = identityRepository.findByProviderAndProviderSubject(provider, subject);

        if (existingIdentityOpt.isPresent()) {
            UserAuthIdentity identity = existingIdentityOpt.get();
            User user = userRepository.findById(identity.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Linked user record not found"));

            if (linkUserId != null && !linkUserId.isBlank()) {
                if (!linkUserId.equals(user.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "PROVIDER_ALREADY_LINKED: This Google account is already linked to another AI Lab account");
                }
                return new OAuthProcessResult(user, false, true, "/settings?oauth=already_linked");
            }

            eligibilityPolicy.assertEligibleForOAuth(user);

            if (!user.isEmailVerified()) {
                user.markEmailVerified();
                userRepository.save(user);
            }
            return new OAuthProcessResult(user, false, false, null);
        }

        // Subject not linked yet
        if (linkUserId != null && !linkUserId.isBlank()) {
            // Explicit linking flow
            User user = userRepository.findById(linkUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for linking"));
            eligibilityPolicy.assertEligibleForOAuth(user);

            if (identityRepository.findByUserIdAndProvider(user.getId(), provider).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "USER_ALREADY_LINKED_PROVIDER: Your account is already linked to a Google account");
            }

            UserAuthIdentity newIdentity = new UserAuthIdentity(user.getId(), provider, subject, normalizedEmail);
            identityRepository.save(newIdentity);

            if (!user.isEmailVerified() && user.getEmail().equalsIgnoreCase(normalizedEmail)) {
                user.markEmailVerified();
                userRepository.save(user);
            }
            return new OAuthProcessResult(user, false, true, "/settings?oauth=linked");
        }

        // Login / Register flow
        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            eligibilityPolicy.assertEligibleForOAuth(existingUser);

            UserAuthIdentity newIdentity = new UserAuthIdentity(existingUser.getId(), provider, subject, normalizedEmail);
            identityRepository.save(newIdentity);

            if (!existingUser.isEmailVerified()) {
                existingUser.markEmailVerified();
                userRepository.save(existingUser);
            }
            return new OAuthProcessResult(existingUser, false, true, null);
        } else {
            // Case D: Auto-provisioning
            String usernameCandidate = generateUniqueUsername(userData.name(), normalizedEmail);
            User newUser = new User(usernameCandidate, normalizedEmail, null);
            if (userData.name() != null && !userData.name().isBlank()) {
                newUser.setDisplayName(userData.name().trim());
            }
            if (userData.picture() != null && !userData.picture().isBlank()) {
                newUser.setAvatarUrl(userData.picture().trim());
            }
            newUser.markEmailVerified();
            newUser = userRepository.save(newUser);

            UserAuthIdentity newIdentity = new UserAuthIdentity(newUser.getId(), provider, subject, normalizedEmail);
            identityRepository.save(newIdentity);

            return new OAuthProcessResult(newUser, true, true, null);
        }
    }

    private String generateUniqueUsername(String name, String email) {
        String base = null;
        if (email != null && email.contains("@")) {
            base = email.substring(0, email.indexOf('@')).replaceAll("[^a-zA-Z0-9_]", "");
        }
        if ((base == null || base.length() < 3) && name != null && !name.isBlank()) {
            base = name.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase(Locale.ROOT);
        }
        if (base == null || base.length() < 3) {
            base = "user";
        }
        if (base.length() > 20) {
            base = base.substring(0, 20);
        }
        if (!userRepository.existsByUsernameIgnoreCase(base)) {
            return base;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 50; i++) {
            String candidate = base + random.nextInt(1000, 9999);
            if (!userRepository.existsByUsernameIgnoreCase(candidate)) {
                return candidate;
            }
        }
        return base + (System.currentTimeMillis() % 100000);
    }
}
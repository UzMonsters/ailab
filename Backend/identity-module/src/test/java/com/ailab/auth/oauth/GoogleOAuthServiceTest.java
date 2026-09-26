package com.ailab.auth.oauth;

import com.ailab.auth.security.AuthenticationEligibilityPolicy;
import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.domain.UserAuthIdentity;
import com.ailab.user.repository.UserAuthIdentityRepository;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAuthIdentityRepository identityRepository;
    @Mock
    private AuthenticationEligibilityPolicy eligibilityPolicy;

    private GoogleOAuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GoogleOAuthServiceImpl(userRepository, identityRepository, eligibilityPolicy);
    }

    @Test
    void rejectsUnverifiedGoogleEmail() {
        OAuthUserData data = new OAuthUserData("google", "sub-123", "unverified@gmail.com", false, "Name", null);

        assertThatThrownBy(() -> service.processOAuthLogin(data, null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void caseA_existingIdentity_authenticatesActiveUser() {
        User user = new User("alice", "alice@gmail.com", "hash");
        user.markEmailVerified();
        UserAuthIdentity identity = new UserAuthIdentity(user.getId(), "google", "sub-123", "alice@gmail.com");

        when(identityRepository.findByProviderAndProviderSubject("google", "sub-123"))
                .thenReturn(Optional.of(identity));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        OAuthUserData data = new OAuthUserData("google", "sub-123", "alice@gmail.com", true, "Alice", null);
        var result = service.processOAuthLogin(data, null);

        assertThat(result.user()).isEqualTo(user);
        assertThat(result.isNewUser()).isFalse();
        verify(eligibilityPolicy).assertEligibleForOAuth(user);
    }

    @Test
    void caseA_existingIdentity_blockedUser_rejects() {
        User user = new User("alice", "alice@gmail.com", "hash");
        user.block("Violation", null);
        UserAuthIdentity identity = new UserAuthIdentity(user.getId(), "google", "sub-123", "alice@gmail.com");

        when(identityRepository.findByProviderAndProviderSubject("google", "sub-123"))
                .thenReturn(Optional.of(identity));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_BLOCKED"))
                .when(eligibilityPolicy).assertEligibleForOAuth(user);

        OAuthUserData data = new OAuthUserData("google", "sub-123", "alice@gmail.com", true, "Alice", null);

        assertThatThrownBy(() -> service.processOAuthLogin(data, null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void caseB_existingLocalUserVerified_autoLinksGoogle() {
        User user = new User("bob", "bob@gmail.com", "hash");
        user.markEmailVerified();

        when(identityRepository.findByProviderAndProviderSubject("google", "sub-bob"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("bob@gmail.com")).thenReturn(Optional.of(user));

        OAuthUserData data = new OAuthUserData("google", "sub-bob", "bob@gmail.com", true, "Bob", null);
        var result = service.processOAuthLogin(data, null);

        assertThat(result.user()).isEqualTo(user);
        assertThat(result.isNewUser()).isFalse();
        verify(identityRepository).save(any(UserAuthIdentity.class));
    }

    @Test
    void caseC_existingLocalUserUnverified_autoLinksAndMarksVerified() {
        User user = new User("carol", "carol@gmail.com", "hash");
        // unverified

        when(identityRepository.findByProviderAndProviderSubject("google", "sub-carol"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("carol@gmail.com")).thenReturn(Optional.of(user));

        OAuthUserData data = new OAuthUserData("google", "sub-carol", "carol@gmail.com", true, "Carol", null);
        var result = service.processOAuthLogin(data, null);

        assertThat(result.user()).isEqualTo(user);
        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
        verify(identityRepository).save(any(UserAuthIdentity.class));
    }

    @Test
    void caseD_newAccountAutoProvisioning() {
        when(identityRepository.findByProviderAndProviderSubject("google", "sub-new"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("newuser@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OAuthUserData data = new OAuthUserData("google", "sub-new", "newuser@gmail.com", true, "New User", "https://avatar.url");
        var result = service.processOAuthLogin(data, null);

        assertThat(result.isNewUser()).isTrue();
        User created = result.user();
        assertThat(created.getEmail()).isEqualTo("newuser@gmail.com");
        assertThat(created.isEmailVerified()).isTrue();
        assertThat(created.hasPassword()).isFalse(); // password_hash is null!
        assertThat(created.getRole()).isEqualTo(Role.USER);
        verify(identityRepository).save(any(UserAuthIdentity.class));
    }

    @Test
    void caseE_explicitLinkingFlow_succeeds() {
        User user = new User("alice", "alice@example.com", "hash");
        when(identityRepository.findByProviderAndProviderSubject("google", "sub-alice"))
                .thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(identityRepository.findByUserIdAndProvider(user.getId(), "google")).thenReturn(Optional.empty());

        OAuthUserData data = new OAuthUserData("google", "sub-alice", "alice.google@gmail.com", true, "Alice G", null);
        var result = service.processOAuthLogin(data, user.getId());

        assertThat(result.user()).isEqualTo(user);
        assertThat(result.isLinked()).isTrue();
        assertThat(result.redirectTarget()).isEqualTo("/settings?oauth=linked");
        verify(identityRepository).save(any(UserAuthIdentity.class));
    }

    @Test
    void caseE_explicitLinkingFlow_conflictIfAlreadyLinkedToAnother() {
        User otherUser = new User("other", "other@example.com", "hash");
        UserAuthIdentity existingIdentity = new UserAuthIdentity(otherUser.getId(), "google", "sub-conflict", "other@gmail.com");

        when(identityRepository.findByProviderAndProviderSubject("google", "sub-conflict"))
                .thenReturn(Optional.of(existingIdentity));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        OAuthUserData data = new OAuthUserData("google", "sub-conflict", "other@gmail.com", true, "Other", null);

        assertThatThrownBy(() -> service.processOAuthLogin(data, "current_user_123"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }
}
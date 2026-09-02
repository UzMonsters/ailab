package com.ailab.user.service;

import com.ailab.auth.token.RefreshTokenService;
import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;
import com.ailab.user.domain.UserDeletedEvent;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {
    @Mock
    UserRepository repository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    ApplicationEventPublisher events;
    @Mock
    com.ailab.auth.token.RefreshTokenOperations refreshTokenService;
    @Mock
    ReAuthTokenOperations reAuthTokenService;

    @InjectMocks
    UserAccountServiceImpl service;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User("alice", "alice@example.com", "encoded_pass");
    }

    @Test
    void registersNewUserWithEncodedPassword() {
        when(repository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
        when(repository.existsByUsernameIgnoreCase("alice")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.register("alice", "alice@example.com", "password");

        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getPasswordHash()).isEqualTo("encoded");
        verify(repository).save(any(User.class));
    }

    @Test
    void rejectsDuplicateEmail() {
        when(repository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register("alice", "alice@example.com", "password"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsDuplicateUsername() {
        when(repository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
        when(repository.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        assertThatThrownBy(() -> service.register("alice", "alice@example.com", "password"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void findsUsersAndMapsResponsesWithoutPassword() {
        alice.updateProfile(null, "avatar.png");
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.UserMeResponse me = service.getMe(alice.getId());
        UserDtos.PublicUserResponse publicProfile = service.getPublic(alice.getId());

        assertThat(me.username()).isEqualTo("alice");
        assertThat(me.avatarUrl()).isEqualTo("avatar.png");
        assertThat(me.language()).isEqualTo("en");
        assertThat(publicProfile).isEqualTo(new UserDtos.PublicUserResponse(
                alice.getId(), "alice", "avatar.png", alice.getLevel(), alice.getXp()));
    }

    @Test
    void patchesProfileSuccessfully() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.PatchProfileRequest patch = new UserDtos.PatchProfileRequest("alice-new", "Alice Wonder", "Bio info");
        UserDtos.UserMeResponse res = service.patchProfile(alice.getId(), patch, null);

        assertThat(res.username()).isEqualTo("alice-new");
        assertThat(res.displayName()).isEqualTo("Alice Wonder");
        assertThat(res.bio()).isEqualTo("Bio info");
    }

    @Test
    void rejectsPatchOnVersionConflict() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.PatchProfileRequest patch = new UserDtos.PatchProfileRequest("alice-new", null, null);
        assertThatThrownBy(() -> service.patchProfile(alice.getId(), patch, "999"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void rejectsPatchOnDuplicateUsername() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));
        when(repository.existsByUsernameIgnoreCaseAndIdNot("bob", alice.getId())).thenReturn(true);

        UserDtos.PatchProfileRequest patch = new UserDtos.PatchProfileRequest("bob", null, null);
        assertThatThrownBy(() -> service.patchProfile(alice.getId(), patch, null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createsAvatarUploadTicketAndRejectsInvalidMimeOrSize() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.AvatarUploadTicketRequest validReq = new UserDtos.AvatarUploadTicketRequest("avatar.png", "image/png", 1024L, "checksum");
        UserDtos.AvatarUploadTicketResponse ticket = service.createAvatarUploadTicket(alice.getId(), validReq);
        assertThat(ticket.assetId()).isNotNull();
        assertThat(ticket.uploadUrl()).contains(alice.getId());
        assertThat(ticket.allowedMimeTypes()).contains("image/png");

        UserDtos.AvatarUploadTicketRequest invalidMime = new UserDtos.AvatarUploadTicketRequest("avatar.exe", "application/x-msdownload", 1024L, "checksum");
        assertThatThrownBy(() -> service.createAvatarUploadTicket(alice.getId(), invalidMime))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        UserDtos.AvatarUploadTicketRequest tooLarge = new UserDtos.AvatarUploadTicketRequest("avatar.png", "image/png", 5_000_000L, "checksum");
        assertThatThrownBy(() -> service.createAvatarUploadTicket(alice.getId(), tooLarge))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    void completesAvatarUpload() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.AvatarCompleteRequest req = new UserDtos.AvatarCompleteRequest("asset_12345", Map.of("x", 0.1));
        UserDtos.AvatarCompleteResponse res = service.completeAvatarUpload(alice.getId(), req);

        assertThat(res.assetId()).isEqualTo("asset_12345");
        assertThat(res.avatarUrl()).contains("asset_12345");
        assertThat(alice.getAvatarUrl()).isEqualTo(res.avatarUrl());
    }

    @Test
    void handlesReAuthentication() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));
        when(passwordEncoder.matches("secret", "encoded_pass")).thenReturn(true);
        when(reAuthTokenService.issueToken(alice.getId())).thenReturn(new ReAuthTokenService.IssuedReAuthToken("token_xyz", Instant.now().plusSeconds(600)));

        UserDtos.ReAuthResponse res = service.reAuthenticate(alice.getId(), new UserDtos.ReAuthRequest("secret"));
        assertThat(res.reauthToken()).isEqualTo("token_xyz");

        when(passwordEncoder.matches("wrong", "encoded_pass")).thenReturn(false);
        assertThatThrownBy(() -> service.reAuthenticate(alice.getId(), new UserDtos.ReAuthRequest("wrong")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(reAuthTokenService).recordFailedAttempt(alice.getId());
    }

    @Test
    void changesPasswordAndRevokesSessions() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));
        when(passwordEncoder.matches("oldPass", "encoded_pass")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("new_encoded_pass");

        service.changePassword(alice.getId(), new UserDtos.ChangePasswordRequest("oldPass", "newPass123"));

        assertThat(alice.getPasswordHash()).isEqualTo("new_encoded_pass");
        verify(refreshTokenService).revokeAll(alice.getId());
    }

    @Test
    void requestsEmailChange() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));
        when(reAuthTokenService.validateAndConsumeToken(alice.getId(), "reauth_123")).thenReturn(true);
        when(repository.existsByEmailIgnoreCaseAndIdNot("newalice@example.com", alice.getId())).thenReturn(false);

        UserDtos.EmailChangeResponse res = service.requestEmailChange(alice.getId(), new UserDtos.EmailChangeRequest("newalice@example.com", "reauth_123", null));

        assertThat(res.pendingEmail()).isEqualTo("newalice@example.com");
        assertThat(alice.getEmail()).isEqualTo("newalice@example.com");
    }

    @Test
    void schedulesAndCancelsAccountDeletion() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));
        when(reAuthTokenService.validateAndConsumeToken(alice.getId(), "reauth_123")).thenReturn(true);

        UserDtos.AccountDeletionResponse res = service.scheduleAccountDeletion(
                alice.getId(), new UserDtos.AccountDeletionRequest("reauth_123", "DELETE")
        );

        assertThat(res.status()).isEqualTo("DELETION_SCHEDULED");
        assertThat(alice.getStatus()).isEqualTo("DELETION_SCHEDULED");
        assertThat(alice.getDeletionId()).isEqualTo(res.deletionId());

        service.cancelAccountDeletion(alice.getId(), res.deletionId());
        assertThat(alice.getStatus()).isEqualTo("ACTIVE");
        assertThat(alice.getDeletionId()).isNull();
    }

    @Test
    void rejectsInvalidConfirmationForDeletion() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> service.scheduleAccountDeletion(alice.getId(), new UserDtos.AccountDeletionRequest("reauth_123", "NO")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void getsLearningProgress() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        UserDtos.LearningProgressResponse res = service.getLearningProgress(alice.getId(), "chemistry");
        assertThat(res.completedLevels()).isEqualTo(1);
        assertThat(res.activeAttempt()).isNotNull();
        assertThat(res.tracks()).isNotEmpty();
    }

    @Test
    void deletesUserAndPublishesDeletionEvent() {
        when(repository.findById(alice.getId())).thenReturn(Optional.of(alice));

        service.delete(alice.getId());

        verify(repository).delete(alice);
        verify(events).publishEvent(new UserDeletedEvent(alice.getId()));
    }
}

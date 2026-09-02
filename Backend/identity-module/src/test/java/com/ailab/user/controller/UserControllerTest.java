package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserAccountService service;

    @InjectMocks
    UserController controller;

    @Test
    void delegatesProfileOperations() {
        UserDtos.UserMeResponse me = new UserDtos.UserMeResponse(
                "usr_1", "alice", "Alice K.", "Bio", "alice@example.com", null, 1, 0,
                "en", "light", Map.of(), Map.of(), List.of(), Instant.now(), Instant.now(), 0L
        );
        UserDtos.PublicUserResponse publicProfile = new UserDtos.PublicUserResponse("usr_2", "bob", "avatar.png", 2, 100);
        UserDtos.UpdateProfileRequest update = new UserDtos.UpdateProfileRequest("alice-new", "avatar.png");
        UserDtos.PatchProfileRequest patch = new UserDtos.PatchProfileRequest("alice-patched", "Alice Patched", "New bio");

        when(service.getMe("usr_1")).thenReturn(me);
        when(service.patchProfile("usr_1", patch, "0")).thenReturn(me);
        when(service.getPublic("usr_2")).thenReturn(publicProfile);

        assertThat(controller.me("usr_1")).isEqualTo(me);
        assertThat(controller.patch("usr_1", patch, "0")).isEqualTo(me);
        assertThat(controller.publicProfile("usr_2")).isEqualTo(publicProfile);
        assertThat(controller.update("usr_1", update)).isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.avatar("usr_1", new UserController.AvatarRequest("new-avatar.png")))
                .isEqualTo(new UserDtos.SuccessResponse(true));

        controller.removeAvatar("usr_1");
        verify(service).removeAvatar("usr_1");

        assertThat(controller.delete("usr_1")).isEqualTo(new UserDtos.SuccessResponse(true));

        UserDtos.UpdatePreferencesRequest preferences = new UserDtos.UpdatePreferencesRequest("uz", "dark", Map.of("notifications", true));
        when(service.getPreferences("usr_1")).thenReturn(new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        when(service.getStatistics("usr_1")).thenReturn(new UserDtos.StatisticsResponse(Map.of("experiments", 4L), List.of("first-experiment")));

        assertThat(controller.preferences("usr_1")).isEqualTo(new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        assertThat(controller.updatePreferences("usr_1", preferences)).isEqualTo(new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        assertThat(controller.statistics("usr_1")).isEqualTo(new UserDtos.StatisticsResponse(Map.of("experiments", 4L), List.of("first-experiment")));

        verify(service).getMe("usr_1");
        verify(service).patchProfile("usr_1", patch, "0");
        verify(service).getPublic("usr_2");
        verify(service).updateProfile("usr_1", "alice-new", "avatar.png");
        verify(service).updateAvatar("usr_1", "new-avatar.png");
        verify(service).delete("usr_1");
        verify(service, times(2)).getPreferences("usr_1");
        verify(service).updatePreferences("usr_1", preferences);
        verify(service).getStatistics("usr_1");
    }

    @Test
    void delegatesExtendedProfileEndpoints() {
        UserDtos.AvatarUploadTicketRequest uploadReq = new UserDtos.AvatarUploadTicketRequest("avatar.webp", "image/webp", 1000L, "sha");
        UserDtos.AvatarUploadTicketResponse uploadRes = new UserDtos.AvatarUploadTicketResponse("asset_1", "https://upload.url", Instant.now(), 2097152L, List.of("image/webp"));
        when(service.createAvatarUploadTicket("usr_1", uploadReq)).thenReturn(uploadRes);

        UserDtos.AvatarCompleteRequest completeReq = new UserDtos.AvatarCompleteRequest("asset_1", Map.of("x", 0));
        UserDtos.AvatarCompleteResponse completeRes = new UserDtos.AvatarCompleteResponse("https://cdn.url/avatar.webp", "asset_1", Instant.now());
        when(service.completeAvatarUpload("usr_1", completeReq)).thenReturn(completeRes);

        UserDtos.ReAuthRequest reAuthReq = new UserDtos.ReAuthRequest("secretPassword");
        UserDtos.ReAuthResponse reAuthRes = new UserDtos.ReAuthResponse("reauth_token_123", Instant.now());
        when(service.reAuthenticate("usr_1", reAuthReq)).thenReturn(reAuthRes);

        UserDtos.EmailChangeRequest emailReq = new UserDtos.EmailChangeRequest("newalice@example.com", "reauth_token_123", null);
        UserDtos.EmailChangeResponse emailRes = new UserDtos.EmailChangeResponse(false, Instant.now(), "newalice@example.com");
        when(service.requestEmailChange("usr_1", emailReq)).thenReturn(emailRes);

        UserDtos.SessionListResponse sessionsRes = new UserDtos.SessionListResponse(List.of(), new UserDtos.PageMetadata(0, 10, 0, 0));
        when(service.getUserSessions("usr_1", 0, 10)).thenReturn(sessionsRes);

        UserDtos.AccountDeletionRequest deletionReq = new UserDtos.AccountDeletionRequest("reauth_token_123", "DELETE");
        UserDtos.AccountDeletionResponse deletionRes = new UserDtos.AccountDeletionResponse("del_123", Instant.now(), "DELETION_SCHEDULED");
        when(service.scheduleAccountDeletion("usr_1", deletionReq)).thenReturn(deletionRes);

        assertThat(controller.createAvatarUploadTicket("usr_1", uploadReq)).isEqualTo(uploadRes);
        assertThat(controller.completeAvatarUpload("usr_1", completeReq)).isEqualTo(completeRes);
        assertThat(controller.reAuth("usr_1", reAuthReq)).isEqualTo(reAuthRes);

        controller.changePassword("usr_1", new UserDtos.ChangePasswordRequest("oldPass", "newPassword123"));
        verify(service).changePassword("usr_1", new UserDtos.ChangePasswordRequest("oldPass", "newPassword123"));

        ResponseEntity<UserDtos.EmailChangeResponse> emailResponse = controller.requestEmailChange("usr_1", emailReq);
        assertThat(emailResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(emailResponse.getBody()).isEqualTo(emailRes);

        assertThat(controller.sessions("usr_1", 0, 10)).isEqualTo(sessionsRes);

        controller.revokeSession("usr_1", "sess_1");
        verify(service).revokeUserSession("usr_1", "sess_1");

        ResponseEntity<UserDtos.AccountDeletionResponse> delResponse = controller.createDeletionRequest("usr_1", deletionReq);
        assertThat(delResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(delResponse.getBody()).isEqualTo(deletionRes);

        controller.cancelDeletionRequest("usr_1", "del_123");
        verify(service).cancelAccountDeletion("usr_1", "del_123");
    }
}

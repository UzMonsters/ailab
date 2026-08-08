package com.ailab.user.controller;

import com.ailab.user.api.UserDtos;
import com.ailab.user.service.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock UserAccountService service;
    @InjectMocks UserController controller;

    @Test
    void delegatesProfileOperations() {
        UserDtos.UserMeResponse me = new UserDtos.UserMeResponse("usr_1", "alice", "alice@example.com", null, 1, 0);
        UserDtos.PublicUserResponse publicProfile = new UserDtos.PublicUserResponse("usr_2", "bob", "avatar.png", 2, 100);
        UserDtos.UpdateProfileRequest update = new UserDtos.UpdateProfileRequest("alice-new", "avatar.png");
        when(service.getMe("usr_1")).thenReturn(me);
        when(service.getPublic("usr_2")).thenReturn(publicProfile);

        assertThat(controller.me("usr_1")).isEqualTo(me);
        assertThat(controller.publicProfile("usr_2")).isEqualTo(publicProfile);
        assertThat(controller.update("usr_1", update)).isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.avatar("usr_1", new UserController.AvatarRequest("new-avatar.png")))
                .isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.removeAvatar("usr_1")).isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.delete("usr_1")).isEqualTo(new UserDtos.SuccessResponse(true));
        UserDtos.UpdatePreferencesRequest preferences = new UserDtos.UpdatePreferencesRequest("uz", "dark", Map.of("notifications", true));
        when(service.getPreferences("usr_1")).thenReturn(new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        when(service.getStatistics("usr_1")).thenReturn(new UserDtos.StatisticsResponse(Map.of("experiments", 4L), java.util.List.of("first-experiment")));
        assertThat(controller.preferences("usr_1")).isEqualTo(new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        assertThat(controller.updatePreferences("usr_1", preferences)).isEqualTo(new UserDtos.SuccessResponse(true));
        assertThat(controller.statistics("usr_1")).isEqualTo(new UserDtos.StatisticsResponse(Map.of("experiments", 4L), java.util.List.of("first-experiment")));

        verify(service).getMe("usr_1");
        verify(service).getPublic("usr_2");
        verify(service).updateProfile("usr_1", "alice-new", "avatar.png");
        verify(service).updateAvatar("usr_1", "new-avatar.png");
        verify(service).removeAvatar("usr_1");
        verify(service).delete("usr_1");
        verify(service).getPreferences("usr_1");
        verify(service).updatePreferences("usr_1", preferences);
        verify(service).getStatistics("usr_1");
    }
}

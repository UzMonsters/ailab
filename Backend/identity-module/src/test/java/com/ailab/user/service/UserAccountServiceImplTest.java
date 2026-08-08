package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;
import com.ailab.user.domain.UserDeletedEvent;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {
    @Mock UserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock ApplicationEventPublisher events;
    @InjectMocks UserAccountServiceImpl service;

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
        User user = new User("alice", "alice@example.com", "hash");
        user.updateProfile(null, "avatar.png");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDtos.UserMeResponse me = service.getMe(user.getId());
        UserDtos.PublicUserResponse publicProfile = service.getPublic(user.getId());

        assertThat(me.username()).isEqualTo("alice");
        assertThat(me.avatarUrl()).isEqualTo("avatar.png");
        assertThat(me.language()).isEqualTo("en");
        assertThat(publicProfile).isEqualTo(new UserDtos.PublicUserResponse(
                user.getId(), "alice", "avatar.png", user.getLevel(), user.getXp()));
    }

    @Test
    void returnsExpectedErrorsForMissingUsers() {
        when(repository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByEmail("missing@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid credentials");
        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updatesAndRemovesAvatar() {
        User user = new User("alice", "alice@example.com", "hash");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        service.updateProfile(user.getId(), "alice-new", "avatar.png");
        assertThat(user.getUsername()).isEqualTo("alice-new");
        assertThat(user.getAvatarUrl()).isEqualTo("avatar.png");

        service.removeAvatar(user.getId());
        assertThat(user.getAvatarUrl()).isNull();
    }

    @Test
    void updatesPreferencesAndReturnsStatistics() {
        User user = new User("alice", "alice@example.com", "hash");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        service.updatePreferences(user.getId(), new UserDtos.UpdatePreferencesRequest("uz", "dark", Map.of("notifications", true)));
        assertThat(service.getPreferences(user.getId())).isEqualTo(
                new UserDtos.PreferencesResponse("uz", "dark", Map.of("notifications", true)));
        assertThat(service.getStatistics(user.getId()).achievements()).isEmpty();
    }

    @Test
    void rejectsDuplicateUsernameDuringProfileUpdate() {
        User user = new User("alice", "alice@example.com", "hash");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.existsByUsernameIgnoreCaseAndIdNot("bob", user.getId())).thenReturn(true);

        assertThatThrownBy(() -> service.updateProfile(user.getId(), "bob", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void deletesUserAndPublishesDeletionEvent() {
        User user = new User("alice", "alice@example.com", "hash");
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        service.delete(user.getId());

        verify(repository).delete(user);
        verify(events).publishEvent(new UserDeletedEvent(user.getId()));
    }
}

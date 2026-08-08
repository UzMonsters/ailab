package com.ailab.user.infrastructure;

import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataSeederTest {
    @Mock UserRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    private UserDataSeeder seeder;

    @Test
    void seedsAdminAndOrdinaryUserWithEncodedPasswordsWhenMissing() {
        seeder = new UserDataSeeder(repository, passwordEncoder,
                "admin", "admin@ailab.local", "Admin@12345",
                "user", "user@ailab.local", "User@12345");
        when(passwordEncoder.encode("Admin@12345")).thenReturn("admin-hash");
        when(passwordEncoder.encode("User@12345")).thenReturn("user-hash");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(2)).save(captor.capture());
        List<User> saved = captor.getAllValues();
        assertThat(saved).extracting(User::getUsername).containsExactlyInAnyOrder("admin", "user");
        assertThat(saved).extracting(User::getRole).containsExactlyInAnyOrder(Role.ADMIN, Role.USER);
        assertThat(saved).extracting(User::getPasswordHash).containsExactlyInAnyOrder("admin-hash", "user-hash");
    }

    @Test
    void skipsBothAccountsWhenTheyAlreadyExist() {
        seeder = new UserDataSeeder(repository, passwordEncoder,
                "admin", "admin@ailab.local", "Admin@12345",
                "user", "user@ailab.local", "User@12345");
        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        seeder.run();

        verify(repository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void skipsAccountWhenUsernameAlreadyExists() {
        seeder = new UserDataSeeder(repository, passwordEncoder,
                "admin", "admin@ailab.local", "Admin@12345",
                "user", "user@ailab.local", "User@12345");
        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(repository.existsByUsernameIgnoreCase("admin")).thenReturn(true);
        when(repository.existsByUsernameIgnoreCase("user")).thenReturn(false);
        when(passwordEncoder.encode("User@12345")).thenReturn("user-hash");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seeder.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("user");
        verify(passwordEncoder, never()).encode("Admin@12345");
    }
}

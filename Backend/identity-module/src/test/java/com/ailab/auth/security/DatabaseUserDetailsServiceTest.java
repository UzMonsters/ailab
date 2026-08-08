package com.ailab.auth.security;

import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {
    @Mock UserRepository repository;

    @Test
    void loadsPasswordAndRoleFromDatabaseUser() {
        User user = new User("admin", "admin@example.com", "stored-bcrypt-hash", Role.ADMIN);
        when(repository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(user));
        DatabaseUserDetailsService service = new DatabaseUserDetailsService(repository);

        var details = service.loadUserByUsername("admin@example.com");

        assertThat(details.getUsername()).isEqualTo("admin@example.com");
        assertThat(details.getPassword()).isEqualTo("stored-bcrypt-hash");
        assertThat(details.getAuthorities()).extracting(authority -> authority.getAuthority())
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void rejectsUnknownDatabaseUser() {
        when(repository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());
        DatabaseUserDetailsService service = new DatabaseUserDetailsService(repository);

        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

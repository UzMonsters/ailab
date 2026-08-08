package com.ailab.user.infrastructure;

import com.ailab.user.domain.Role;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class UserDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(UserDataSeeder.class);
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SeedAccount admin;
    private final SeedAccount user;

    public UserDataSeeder(UserRepository repository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.seed.admin.username}") String adminUsername,
                          @Value("${app.seed.admin.email}") String adminEmail,
                          @Value("${app.seed.admin.password}") String adminPassword,
                          @Value("${app.seed.user.username}") String userUsername,
                          @Value("${app.seed.user.email}") String userEmail,
                          @Value("${app.seed.user.password}") String userPassword) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.admin = new SeedAccount(adminUsername, adminEmail, adminPassword, Role.ADMIN);
        this.user = new SeedAccount(userUsername, userEmail, userPassword, Role.USER);
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedIfMissing(admin);
        seedIfMissing(user);
    }

    private void seedIfMissing(SeedAccount account) {
        boolean emailExists = repository.existsByEmailIgnoreCase(account.email());
        boolean usernameExists = repository.existsByUsernameIgnoreCase(account.username());
        if (emailExists || usernameExists) {
            log.info("Skipping seed account '{}' because it already exists", account.username());
            return;
        }

        User seededUser = new User(account.username(), account.email(),
                passwordEncoder.encode(account.password()), account.role());
        repository.save(seededUser);
        log.info("Seeded {} account '{}'", account.role(), account.username());
    }

    private record SeedAccount(String username, String email, String password, Role role) { }
}

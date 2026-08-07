package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.*;
import com.ailab.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    public UserAccountServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder,
                                  ApplicationEventPublisher events) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.events = events;
    }

    @Override
    public User register(String username, String email, String rawPassword) {
        if (repository.existsByEmailIgnoreCase(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        if (repository.existsByUsernameIgnoreCase(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        return repository.save(new User(username, email, passwordEncoder.encode(rawPassword)));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.UserMeResponse getMe(String id) {
        User u = findById(id);
        return toMeResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.PreferencesResponse getPreferences(String id) {
        User u = findById(id);
        return new UserDtos.PreferencesResponse(u.getLanguage(), u.getTheme(), u.getApplicationSettings());
    }

    @Override
    public void updatePreferences(String id, UserDtos.UpdatePreferencesRequest request) {
        findById(id).updatePreferences(request.language(), request.theme(), request.applicationSettings());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.StatisticsResponse getStatistics(String id) {
        User u = findById(id);
        return new UserDtos.StatisticsResponse(u.getStatistics(), u.getAchievements().stream().sorted().toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.PublicUserResponse getPublic(String id) {
        User u = findById(id);
        return new UserDtos.PublicUserResponse(u.getId(), u.getUsername(), u.getAvatarUrl(), u.getLevel(), u.getXp());
    }

    @Override
    public void updateProfile(String id, String username, String avatarUrl) {
        User user = findById(id);
        if (username != null && !username.equalsIgnoreCase(user.getUsername())
                && repository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        user.updateProfile(username, avatarUrl);
    }

    @Override
    public void delete(String id) {
        User user = findById(id);
        user.incrementTokenVersion();
        repository.delete(user);
        events.publishEvent(new UserDeletedEvent(id));
    }

    @Override
    public void updateAvatar(String id, String avatarUrl) {
        findById(id).updateProfile(null, avatarUrl);
    }

    @Override
    public void removeAvatar(String id) {
        findById(id).removeAvatar();
    }

    @Override
    public void invalidateSessions(String id) {
        findById(id).incrementTokenVersion();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDtos.AdminUserResponse> getAllUsers() {
        return repository.findAll().stream().map(this::toAdminResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDtos.AdminUserResponse getAdminUser(String id) {
        return toAdminResponse(findById(id));
    }

    @Override
    public void updateAdminUser(String id, UserDtos.AdminUpdateUserRequest request) {
        User user = findById(id);
        if (request.username() != null && !request.username().equalsIgnoreCase(user.getUsername())
                && repository.existsByUsernameIgnoreCaseAndIdNot(request.username(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())
                && repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }
        user.updateAdminProfile(request.username(), request.email(), request.role());
    }

    @Override
    public void deleteAdminUser(String id) {
        User user = findById(id);
        user.incrementTokenVersion();
        repository.delete(user);
        events.publishEvent(new UserDeletedEvent(id));
    }

    private UserDtos.UserMeResponse toMeResponse(User u) {
        return new UserDtos.UserMeResponse(u.getId(), u.getUsername(), u.getEmail(), u.getAvatarUrl(),
                u.getLevel(), u.getXp(), u.getLanguage(), u.getTheme(), u.getApplicationSettings(),
                u.getStatistics(), u.getAchievements().stream().sorted().toList());
    }

    private UserDtos.AdminUserResponse toAdminResponse(User u) {
        return new UserDtos.AdminUserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.getAvatarUrl(),
                u.getLevel(), u.getXp(), u.getLanguage(), u.getTheme(), u.getApplicationSettings(),
                u.getStatistics(), u.getAchievements().stream().sorted().toList());
    }
}

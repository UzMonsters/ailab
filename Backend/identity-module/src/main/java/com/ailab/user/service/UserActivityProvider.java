package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;

import java.time.Instant;
import java.util.List;

public interface UserActivityProvider {

    List<UserDtos.UserActivity> getUserActivities(User user, Instant from, Instant to, String type);
}

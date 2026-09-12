package com.ailab.user.service;

import com.ailab.user.api.UserDtos;
import com.ailab.user.domain.User;

import java.util.Optional;

public interface UserLearningProgressProvider {

    Optional<UserDtos.LearningProgressResponse> getLearningProgress(User user, String track);

    Optional<UserDtos.UserLearningProgressResponse> getUserLearningProgress(User user, String track);
}

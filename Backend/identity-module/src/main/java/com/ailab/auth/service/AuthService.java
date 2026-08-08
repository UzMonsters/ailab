package com.ailab.auth.service;

import com.ailab.auth.api.AuthDtos;

public interface AuthService {
    AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest request);

    AuthDtos.AuthenticationResult login(AuthDtos.LoginRequest request);

    AuthDtos.AuthenticationResult refresh(String token);

    void logout(String token);
}

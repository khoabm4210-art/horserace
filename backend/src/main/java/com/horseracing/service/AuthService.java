package com.horseracing.service;

import com.horseracing.dto.request.auth.LoginRequest;
import com.horseracing.dto.request.auth.RegisterRequest;
import com.horseracing.dto.request.auth.RefreshTokenRequest;
import com.horseracing.dto.response.auth.LoginResponse;
import com.horseracing.dto.response.auth.TokenRefreshResponse;
import com.horseracing.dto.response.user.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    
    UserResponse register(RegisterRequest request);
    
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);
    
    void logout(String refreshToken);
    
    UserResponse getCurrentUser(Long userId);
}

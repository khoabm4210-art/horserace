package com.horseracing.controller;

import com.horseracing.dto.request.auth.LoginRequest;
import com.horseracing.dto.request.auth.RefreshTokenRequest;
import com.horseracing.dto.request.auth.RegisterRequest;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.dto.response.auth.LoginResponse;
import com.horseracing.dto.response.auth.TokenRefreshResponse;
import com.horseracing.dto.response.user.UserResponse;
import com.horseracing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng ký thành công", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", response));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token được làm mới", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Đăng xuất thành công", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = authService.getCurrentUser(username);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

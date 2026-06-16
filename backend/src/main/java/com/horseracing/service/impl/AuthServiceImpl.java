package com.horseracing.service.impl;

import com.horseracing.dto.request.auth.LoginRequest;
import com.horseracing.dto.request.auth.RefreshTokenRequest;
import com.horseracing.dto.request.auth.RegisterRequest;
import com.horseracing.dto.response.auth.LoginResponse;
import com.horseracing.dto.response.auth.TokenRefreshResponse;
import com.horseracing.dto.response.auth.UserLoginResponse;
import com.horseracing.dto.response.user.UserResponse;
import com.horseracing.entity.RefreshToken;
import com.horseracing.entity.User;
import com.horseracing.enums.Role;
import com.horseracing.enums.UserStatus;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.UnauthorizedException;
import com.horseracing.repository.RefreshTokenRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.security.JwtTokenProvider;
import com.horseracing.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .role(request.getRole() != null ? request.getRole() : Role.SPECTATOR)
            .status(UserStatus.ACTIVE)
            .deleted(0)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return mapToUserResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

            String accessToken = tokenProvider.generateAccessToken(user.getUsername(), user.getId());
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername(), user.getId());

            // Save refresh token to database
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(0)
                .build();
            refreshTokenRepository.save(refreshTokenEntity);

            log.info("User logged in: {}", user.getUsername());

            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .user(mapToUserLoginResponse(user))
                .build();
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getIsRevoked() == 1 || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired or been revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = tokenProvider.generateAccessToken(user.getUsername(), user.getId());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUsername(), user.getId());

        // Revoke old refresh token
        refreshToken.setIsRevoked(1);
        refreshTokenRepository.save(refreshToken);

        // Save new refresh token
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
            .user(user)
            .token(newRefreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .isRevoked(0)
            .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("Token refreshed for user: {}", user.getUsername());

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(tokenProvider.getAccessTokenExpiration())
            .build();
    }

    @Override
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        token.setIsRevoked(1);
        refreshTokenRepository.save(token);
        log.info("User logged out");
    }

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .status(user.getStatus().name())
            .avatarUrl(user.getAvatarUrl())
            .createdAt(user.getCreatedAt().toString())
            .build();
    }

    private UserLoginResponse mapToUserLoginResponse(User user) {
        return UserLoginResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .role(user.getRole().name())
            .status(user.getStatus().name())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }
}

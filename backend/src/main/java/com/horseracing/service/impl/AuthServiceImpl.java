package com.horseracing.service.impl;

import com.horseracing.dto.request.auth.LoginRequest;
import com.horseracing.dto.request.auth.RegisterRequest;
import com.horseracing.dto.request.auth.RefreshTokenRequest;
import com.horseracing.dto.response.auth.LoginResponse;
import com.horseracing.dto.response.auth.TokenRefreshResponse;
import com.horseracing.dto.response.user.UserResponse;
import com.horseracing.entity.RefreshToken;
import com.horseracing.entity.User;
import com.horseracing.enums.Role;
import com.horseracing.enums.UserStatus;
import com.horseracing.exception.UnauthorizedException;
import com.horseracing.repository.RefreshTokenRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.security.JwtTokenProvider;
import com.horseracing.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("User account is not active");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        RefreshToken refreshToken = createRefreshToken(user);

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : Role.SPECTATOR)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("User account is not active");
        }

        // Revoke old token and create new one
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newRefreshToken = createRefreshToken(user);
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

        log.info("Token refreshed for user: {}", user.getUsername());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
        log.info("User logged out");
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}

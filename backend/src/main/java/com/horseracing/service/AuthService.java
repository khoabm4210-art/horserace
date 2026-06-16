package com.horseracing.service;

import com.horseracing.dto.auth.LoginRequest;
import com.horseracing.dto.auth.LoginResponse;
import com.horseracing.dto.auth.RegisterRequest;
import com.horseracing.dto.auth.TokenRefreshRequest;
import com.horseracing.dto.auth.TokenRefreshResponse;
import com.horseracing.dto.user.UserResponse;
import com.horseracing.entity.RefreshToken;
import com.horseracing.entity.User;
import com.horseracing.enums.Role;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.UnauthorizedException;
import com.horseracing.repository.RefreshTokenRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        // Create new user
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .role(request.getRole() != null ? request.getRole() : Role.SPECTATOR)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findActiveByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Save refresh token
        RefreshToken tokenEntity = RefreshToken.builder()
            .userId(user.getId())
            .token(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();
        refreshTokenRepository.save(tokenEntity);

        log.info("User logged in successfully: {}", user.getId());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600000L) // 1 hour in milliseconds
            .user(modelMapper.map(user, UserResponse.class))
            .build();
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        log.info("Refreshing token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Revoke old refresh token and save new one
        refreshTokenRepository.delete(refreshToken);
        RefreshToken newTokenEntity = RefreshToken.builder()
            .userId(user.getId())
            .token(newRefreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();
        refreshTokenRepository.save(newTokenEntity);

        log.info("Token refreshed successfully for user: {}", user.getId());

        return TokenRefreshResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600000L)
            .build();
    }

    @Transactional
    public void logout(Long userId, String refreshToken) {
        log.info("Logout for user: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
    }

    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("User not found"));
        return modelMapper.map(user, UserResponse.class);
    }
}

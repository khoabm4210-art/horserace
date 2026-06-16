package com.horseracing.service.impl;

import com.horseracing.dto.request.user.UserCreateRequest;
import com.horseracing.dto.request.user.UserUpdateRequest;
import com.horseracing.dto.response.user.UserResponse;
import com.horseracing.entity.User;
import com.horseracing.enums.Role;
import com.horseracing.enums.UserStatus;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, String keyword, Role role, String status) {
        // TODO: Implement filtering by keyword, role, status
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
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
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User created: {}", user.getUsername());
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        user = userRepository.save(user);
        log.info("User updated: {}", user.getUsername());
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse lockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.LOCKED);
        user = userRepository.save(user);
        log.info("User locked: {}", user.getUsername());
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse unlockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        log.info("User unlocked: {}", user.getUsername());
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse changeUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(newRole);
        user = userRepository.save(user);
        log.info("User role changed: {} -> {}", user.getUsername(), newRole);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(true);
        userRepository.save(user);
        log.info("User deleted (soft): {}", user.getUsername());
    }
}

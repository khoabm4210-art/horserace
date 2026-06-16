package com.horseracing.service;

import com.horseracing.dto.user.UserResponse;
import com.horseracing.entity.User;
import com.horseracing.enums.Role;
import com.horseracing.enums.UserStatus;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable)
            .map(user -> modelMapper.map(user, UserResponse.class));
    }

    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
            .map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Transactional
    public UserResponse updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updatedUser.getFullName() != null) {
            user.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAvatarUrl() != null) {
            user.setAvatarUrl(updatedUser.getAvatarUrl());
        }

        user.setUpdatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User updated: {}", id);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Transactional
    public UserResponse lockUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.LOCKED);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User locked: {}", id);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Transactional
    public UserResponse unlockUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User unlocked: {}", id);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Transactional
    public UserResponse changeUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(newRole);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User role changed to {}: {}", newRole, id);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(1);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        log.info("User deleted: {}", id);
    }
}

package com.horseracing.service;

import com.horseracing.dto.request.user.UserCreateRequest;
import com.horseracing.dto.request.user.UserUpdateRequest;
import com.horseracing.dto.response.user.UserResponse;
import com.horseracing.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable, String keyword, Role role, String status);
    
    UserResponse getUserById(Long id);
    
    UserResponse createUser(UserCreateRequest request);
    
    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    UserResponse lockUser(Long id);
    
    UserResponse unlockUser(Long id);
    
    UserResponse changeUserRole(Long id, Role newRole);
    
    void deleteUser(Long id);
}

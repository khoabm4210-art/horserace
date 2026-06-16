package com.horseracing.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;
}

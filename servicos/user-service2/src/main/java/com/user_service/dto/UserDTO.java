package com.user_service.dto;

import com.user_service.entity.UserRole;

public record UserDTO(
        Long id,
        String nome,
        String userName,
        String email,
        UserRole role
) {
}

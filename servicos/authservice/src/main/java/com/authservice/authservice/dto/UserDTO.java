package com.authservice.authservice.dto;


import com.authservice.authservice.enums.UserRole;

public record UserDTO(
        Long id,
        String nome,
        String userName,
        String email,
        UserRole userRole
) {
}

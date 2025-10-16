package com.authservice.authservice.dto;

import com.authservice.authservice.enums.UserRole;

public record UserResponseDTO(String username, UserRole role) {
}

package com.webservice.feedbackservice.sistema.dto;


import com.webservice.feedbackservice.sistema.enums.UserRole;

public record UserDTOrecord(
        Long id,
        String nome,
        String userName,
        String email,
        UserRole userRole
) {
}

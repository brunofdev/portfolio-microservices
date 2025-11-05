package com.mailservice.dto;

public record UserDTO(
        Long id,
        String nome,
        String userName,
        String email
){}

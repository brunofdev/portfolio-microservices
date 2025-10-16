package com.authservice.authservice.dto;


import com.authservice.authservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nome;
    private String userName;
    private String email;
    private UserRole role;
}


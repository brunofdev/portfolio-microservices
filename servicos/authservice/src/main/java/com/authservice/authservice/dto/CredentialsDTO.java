package com.authservice.authservice.dto;

import jakarta.validation.constraints.NotBlank; // Importe a anotação
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {

    @NotBlank(message = "O nome de usuário não pode estar em branco.")
    public String userName;

    @NotBlank(message = "A senha não pode estar em branco.")
    public String password;
}
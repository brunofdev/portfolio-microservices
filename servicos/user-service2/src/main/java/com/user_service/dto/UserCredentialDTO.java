package com.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCredentialDTO {
    @NotBlank(message = "O nome de usuário não pode estar em branco.")
    @Size(min = 5, max = 15, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
    String userName;
    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    String password;

    public void setUserName(String userName) {
        this.userName = userName.toUpperCase();
    }


}

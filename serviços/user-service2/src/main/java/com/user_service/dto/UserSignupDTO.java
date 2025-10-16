package com.user_service.dto;

import com.user_service.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDTO {
    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 5, max = 100, message = "O nome deve ter entre 5 e 100 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ú\\s'-]+$", message = "O nome deve conter apenas letras, espaços e hifens/apóstrofos.")
    private String name;

    @NotBlank(message = "O nome de usuário não pode estar em branco.")
    @Size(min = 5, max = 20, message = "O nome de usuário deve ter entre 5 e 20 caracteres.")
    @Pattern(regexp = "\\S+", message = "O nome de usuário não pode conter espaços em branco.")
    private String userName;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message =
                    "A senha deve conter no mínimo 8 caracteres, " +
                    "com pelo menos uma letra maiúscula, uma minúscula," +
                            " um número e um caractere especial (@$!%*?&).")
    private String password;

    @Email(message = "O formato do e-mail é inválido.")
    @Pattern(regexp = "^$|\\S+", message = "O e-mail não pode conter espaços em branco.")
    private String email;

    private UserRole userRole;

    public void setUserName(String userName) {
            this.userName = userName.toUpperCase();
    }
    public void setEmail(String email) {
        this.email = email.toUpperCase();
    }

}
package com.webservice.feedbackservice.sistema.dto;

import com.webservice.feedbackservice.sistema.enums.UserRole;
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
    private UserRole userRole;


    public String getUserName(){
        return userName.toUpperCase();
    }
}



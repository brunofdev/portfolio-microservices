package com.user_service.controller;

import com.user_service.dto.UserDTO;
import com.user_service.dto.apiresponse.ApiResponse;
import com.user_service.dto.UserCredentialDTO;
import com.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/users") // Um prefixo exclusivo para rotas internas
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/validate-credential")
    public ResponseEntity<ApiResponse<UserDTO>> validateCredentials(@Valid @RequestBody UserCredentialDTO userCredentialDTO){
        UserDTO userDTO = userService.authenticate(userCredentialDTO);
        return ResponseEntity.ok().body(ApiResponse.success("Credenciais válidadas", userDTO));
    }
    @PostMapping("/details")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersDatils(@RequestBody List<String> userNames){
        List<UserDTO> foundUsers = userService.getUsersByUsernames(userNames);
        return ResponseEntity.ok().body(ApiResponse.success("Recurso encontrado", foundUsers));
    }
}
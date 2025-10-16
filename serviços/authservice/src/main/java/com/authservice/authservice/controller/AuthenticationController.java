package com.authservice.authservice.controller;

import com.authservice.authservice.dto.UserDTO;
import com.authservice.authservice.dto.UserResponseDTO;
import com.authservice.authservice.dto.apiresponse.ApiResponse;
import com.authservice.authservice.dto.AuthResponseDTO;
import com.authservice.authservice.dto.CredentialsDTO;
import com.authservice.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody CredentialsDTO credentialsDTO) {
        // 1. O serviço agora retorna um Map com o token e o UserDTO
        Map<String, Object> authResult = authenticationService.login(credentialsDTO);
        String jwtToken = (String) authResult.get("token");
        UserDTO user = (UserDTO) authResult.get("user");

        // 2. Cria o DTO de usuário para a resposta
        UserResponseDTO userResponse = new UserResponseDTO(user.getUserName(), user.getRole());

        // 3. Cria o DTO de resposta final, combinando o token e os dados do usuário
        AuthResponseDTO responseDTO = new AuthResponseDTO(jwtToken, userResponse);

        // 4. Embrulha no seu ApiResponse padrão e retorna
        return ResponseEntity.ok(ApiResponse.success("Usuário autenticado com sucesso", responseDTO));
    }
}

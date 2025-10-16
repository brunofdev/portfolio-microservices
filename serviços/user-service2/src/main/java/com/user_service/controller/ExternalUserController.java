package com.user_service.controller;

import com.user_service.dto.apiresponse.ApiResponse;
import com.user_service.dto.UserDTO;
import com.user_service.dto.UserSignupDTO;
import com.user_service.entity.User;
import com.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class ExternalUserController {
    /*Todas as rotas e permissões são gerenciadas pelo serviço de gateway
    //Com isso, nenhuma requisição direta para o endereço deste serviço
    //é aceita, todas requisições devem inicar com o sufixo abaixo:
    //https://apigateway-kgvz.onrender.com/ patch da api requisitada
    //por exemplo: https://apigateway-kgvz.onrender.com/api/users/register*/

    private final UserService userService;

    @Autowired
    public ExternalUserController(UserService userService){
        this.userService = userService;
    }

    //rota publica
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody UserSignupDTO userSignupDTO){
        UserDTO userDTO = userService.createNewUser(userSignupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Recurso Criado", userDTO));
    }
    //somente para role admin
    @GetMapping("/getusers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(){
        return ResponseEntity.ok().body(ApiResponse.success("Recurso Disponivel", userService.listAllUsers()));
    }
    //somente para role admin
    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable long id){
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso deletado", null));
    }
    //somente para role admin
    @DeleteMapping("/deleteuserbyusername/{userName}")
    public ResponseEntity<ApiResponse> deleteUserByUserName(@PathVariable String userName){
        userService.deleteUserByUserName(userName);
        return ResponseEntity.ok(ApiResponse.success("Recurso deletado", userName));
    }
}

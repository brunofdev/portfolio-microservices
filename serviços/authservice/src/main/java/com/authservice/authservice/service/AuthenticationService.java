package com.authservice.authservice.service;

import com.authservice.authservice.dto.UserDTO;
import com.authservice.authservice.dto.apiresponse.ApiResponse;
import com.authservice.authservice.dto.CredentialsDTO;
import com.authservice.authservice.exceptions.InvalidCredentialsException;
import com.authservice.authservice.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AuthenticationService {

    private final WebClient.Builder webClientBuilder;
    private final JwtProvider jwtProvider;

    @Value("${userservice.api.url}")
    private String userServiceUrl;

    @Value("${api.internal.secret}")
    private String internalApiSecret;

    public AuthenticationService(WebClient.Builder webClientBuilder, JwtProvider jwtProvider){
        this.webClientBuilder = webClientBuilder;
        this.jwtProvider = jwtProvider;
    }

    private UserDTO validateCredentialsWithUserService(CredentialsDTO credentials) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();

        // A chamada agora espera receber um corpo de resposta
        ApiResponse<UserDTO> apiResponse = webClient.post()
                .uri("/internal/users/validate-credential")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-Internal-Secret", internalApiSecret)
                .body(Mono.just(credentials), CredentialsDTO.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new InvalidCredentialsException("Usuário ou senha inválidas"))
                )
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserDTO>>() {})
                .block();

        // MUDANÇA 3: Verificamos o campo "status" dentro do JSON recebido
        if (apiResponse == null || !apiResponse.getStatus()) {
            throw new InvalidCredentialsException("A validação de credenciais falhou (status false retornado pelo user-service).");
        }
        return apiResponse.getDados();
    }

    public Map<String, Object> login(CredentialsDTO credentials) {
        UserDTO userData = validateCredentialsWithUserService(credentials);
        String token = jwtProvider.generateToken(userData.getUserName(), userData.getRole());
        // Retorna um mapa com o token e os dados do usuário
        return Map.of("token", token, "user", userData);
    }
}

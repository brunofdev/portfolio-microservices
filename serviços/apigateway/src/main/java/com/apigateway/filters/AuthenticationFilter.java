package com.apigateway.filters;

import com.apigateway.enums.UserRole;
import com.apigateway.exceptions.InvalidAuthHeaderException;
import com.apigateway.exceptions.InvalidTokenJwtException;
import com.apigateway.exceptions.UserForbiddenException;
import com.apigateway.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Filtro global de autenticação e autorização para o API Gateway.
 * Responsável por interceptar todas as requisições, validar tokens JWT
 * e aplicar regras de acesso baseadas em permissões (roles).
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    @Value("${api.internal.secret}")
    private String internalApiSecret;

    //Mapa estático para centralizar as regras de autorização.
    //Define a permissão mínima necessária para acessar rotas que começam com um determinado prefixo.
    private static final Map<String, UserRole> PROTECTED_ROUTES = Map.of(
            "/api/users/getusers", UserRole.ADMIN,
            "/api/users/deleteuser", UserRole.ADMIN,
            "/api/users/deleteuserbyusername", UserRole.ADMIN,
            "/api/feedback/deletefeedback", UserRole.ADMIN
            // Adicionar novas regras de autorização aqui. Ex: "/api/admin", UserRole.ADMIN
    );
    //Lista de endpoints públicos que não requerem autenticação JWT.
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/users/register",
            "/api/feedback/getallfeedbacks"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Permite requisições OPTIONS (preflight do CORS) sem qualquer validação.
            if (request.getMethod() == HttpMethod.OPTIONS) {
                return chain.filter(exchange);
            }

            // Verifica se a rota é pública. Se for, enriquece com o header interno e a deixa passar.
            if (isPublicEndpoint(path)) {
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Internal-Secret", internalApiSecret)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }

            try {
                // --- A partir daqui, a rota é considerada protegida ---
                // 1. AUTENTICAÇÃO: Valida o token JWT.
                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                String token = extractTokenFromHeader(authHeader);
                validateToken(token);

                // 2. AUTORIZAÇÃO: Extrai a role e verifica as permissões de acesso à rota.
                UserRole userRole = jwtUtil.extractUserRole(token);
                authorizeRequest(path, userRole);

                // 3. ENRIQUECIMENTO: Se a autenticação e autorização foram bem-sucedidas,
                // enriquece a requisição com headers de contexto para os serviços internos.
                String username = jwtUtil.extractUsername(token);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Authenticated-User-Role", userRole.name())
                        .header("X-Authenticated-User", username)
                        .header("X-Internal-Secret", internalApiSecret)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (InvalidAuthHeaderException | InvalidTokenJwtException e) {
                // Captura falhas de autenticação e retorna 401 Unauthorized.
                return this.onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
            } catch (UserForbiddenException e) {
                // Captura falhas de autorização e retorna 403 Forbidden.
                return this.onError(exchange, e.getMessage(), HttpStatus.FORBIDDEN);
            }
        };
    }
    // --- MÉTODOS AUXILIARES (HELPERS) ---
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
    /**
     * Extrai o token do cabeçalho "Authorization", validando o formato "Bearer".
     * @throws InvalidAuthHeaderException se o header for nulo ou malformado.
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthHeaderException("Cabeçalho de autorização ausente ou malformado.");
        }
        return authHeader.substring(7);
    }
    /**
     * Valida a assinatura e a data de expiração do token JWT.
     * @throws InvalidTokenJwtException se o token for inválido.
     */
    private void validateToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new InvalidTokenJwtException("Token JWT inválido ou expirado.");
        }
    }
    /**
     * Verifica se a role do usuário é suficiente para acessar a rota solicitada,
     * com base nas regras definidas no mapa PROTECTED_ROUTES.
     * @throws UserForbiddenException se a permissão for negada.
     */
    private void authorizeRequest(String path, UserRole userRole) {
        for (Map.Entry<String, UserRole> entry : PROTECTED_ROUTES.entrySet()) {
            String routePrefix = entry.getKey();
            UserRole requiredRole = entry.getValue();

            if (path.startsWith(routePrefix)) {
                // Compara a ordem do Enum (USER=0, ADMIN=1).
                // Se a role do usuário for menor que a requerida, o acesso é negado.
                if (userRole.ordinal() < requiredRole.ordinal()) {
                    throw new UserForbiddenException("Acesso Negado: Requer permissão de " + requiredRole.name());
                }
                return; // Permissão concedida, não precisa checar outras regras.
            }
        }
    }
    /**
     * Manipulador centralizado para retornar respostas de erro HTTP.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    public static class Config {
        // Classe de configuração vazia, necessária para AbstractGatewayFilterFactory.
    }
}
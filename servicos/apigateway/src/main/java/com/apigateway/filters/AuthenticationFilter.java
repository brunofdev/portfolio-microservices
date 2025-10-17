package com.apigateway.filters;

import com.apigateway.enums.UserRole;
import com.apigateway.exceptions.InvalidAuthHeaderException;
import com.apigateway.exceptions.InvalidTokenJwtException;
import com.apigateway.exceptions.UserForbiddenException;
import com.apigateway.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter; // Apenas GlobalFilter
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Filtro global de autenticacao e autorizacao para o API Gateway.
 * Implementa GlobalFilter para ser aplicado automaticamente a todas as rotas.
 * A anotacao @Order(-1) garante que ele rode antes dos filtros de roteamento.
 */
@Component
@Order(-1)
public class AuthenticationFilter implements GlobalFilter { // CORRECAO: Removemos , GatewayFilter

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    @Value("${api.internal.secret}")
    private String internalApiSecret;

    // Mapa estatico para centralizar as regras de autorizacao.
    private static final Map<String, UserRole> PROTECTED_ROUTES = Map.of(
            "/api/users/getusers", UserRole.ADMIN,
            "/api/users/deleteuser", UserRole.ADMIN,
            "/api/users/deleteuserbyusername", UserRole.ADMIN,
            "/api/feedback/deletefeedback", UserRole.ADMIN
    );

    // Lista de endpoints publicos da API (que precisam do header secreto)
    private static final List<String> PUBLIC_API_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/users/register",
            "/api/feedback/getallfeedbacks"
    );

    // Lista de endpoints de monitoramento (que NAO precisam de header secreto)
    private static final List<String> ACTUATOR_ENDPOINTS = List.of(
            "/actuator/prometheus"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        logger.debug("Interceptando requisicao: {} {}", request.getMethod(), path);

        // 1. Libera requisicoes OPTIONS (CORS) imediatamente, sem modificacao
        if (request.getMethod() == HttpMethod.OPTIONS) {
            logger.trace("Requisição OPTIONS detectada — liberando sem autenticacao.");
            return chain.filter(exchange);
        }

        // 2. Libera endpoints do Actuator (Prometheus) imediatamente, sem modificacao
        if (isActuatorEndpoint(path)) {
            logger.trace("Endpoint Actuator detectado: {}", path);
            return chain.filter(exchange);
        }

        // 3. Libera endpoints publicos (Login, Register), mas ADICIONA o header secreto
        if (isPublicApiEndpoint(path)) {
            logger.debug("Endpoint publico da API acessado: {}", path);
            return chain.filter(addInternalSecretHeader(exchange));
        }

        // 4. Se nao for nenhum dos acima, e uma rota protegida.
        // Inicia a validacao de Autenticacao e Autorizacao.
        try {
            String authHeader = extractAuthHeader(request);
            String token = extractToken(authHeader);
            validateToken(token);

            UserRole userRole = jwtUtil.extractUserRole(token); // Corrija para extractRole se o nome for esse
            String username = jwtUtil.extractUsername(token);

            logger.info("Usuario autenticado: {} | Role: {} | Endpoint: {}", username, userRole, path);

            // Verifica se o usuario tem permissao
            authorizeRequest(path, userRole);

            // Monta a requisicao final com todos os headers de contexto
            ServerHttpRequest authenticatedRequest = request.mutate()
                    .headers(headers -> {
                        headers.set("X-Authenticated-User-Role", userRole.name());
                        headers.set("X-Authenticated-User", username);
                        headers.set("X-Internal-Secret", internalApiSecret);
                    })
                    .build();

            return chain.filter(exchange.mutate().request(authenticatedRequest).build());

        } catch (InvalidAuthHeaderException | InvalidTokenJwtException e) {
            logger.warn("Falha de autenticacao: {}", e.getMessage());
            return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserForbiddenException e) {
            logger.warn("Acesso negado: {}", e.getMessage());
            return onError(exchange, e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Erro inesperado no filtro de autenticacao", e);
            return onError(exchange, "Erro interno no gateway.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Metodos auxiliares (Helpers) ---

    // Metodo renomeado para maior clareza
    private boolean isPublicApiEndpoint(String path) {
        return PUBLIC_API_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isActuatorEndpoint(String path) {
        return ACTUATOR_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    // Unico local que faz a mutacao (adicao de header)
    private ServerWebExchange addInternalSecretHeader(ServerWebExchange exchange) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.set("X-Internal-Secret", internalApiSecret))
                .build();
        return exchange.mutate().request(mutatedRequest).build();
    }

    private String extractAuthHeader(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            throw new InvalidAuthHeaderException("Cabeçalho de autorização ausente.");
        }
        return header;
    }

    private String extractToken(String header) {
        if (!header.startsWith("Bearer ")) {
            throw new InvalidAuthHeaderException("Token JWT malformado. Esperado prefixo 'Bearer '.");
        }
        return header.substring(7);
    }

    private void validateToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new InvalidTokenJwtException("Token JWT inválido ou expirado.");
        }
    }

    private void authorizeRequest(String path, UserRole userRole) {
        // Itera sobre as regras protegidas
        for (Map.Entry<String, UserRole> entry : PROTECTED_ROUTES.entrySet()) {
            String routePrefix = entry.getKey();
            UserRole requiredRole = entry.getValue();

            if (path.startsWith(routePrefix)) {
                // Compara a ordem do Enum (USER=0, ADMIN=1).
                if (userRole.ordinal() < requiredRole.ordinal()) {
                    throw new UserForbiddenException("Acesso negado: requer permissão de " + requiredRole.name());
                }
                return; // Permissao concedida
            }
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        // Retorna um JSON padronizado no corpo do erro
        String jsonResponse = String.format("{\"timestamp\":\"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                java.time.Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message);

        return response.writeWith(Mono.just(
                response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8))
        ));
    }
}
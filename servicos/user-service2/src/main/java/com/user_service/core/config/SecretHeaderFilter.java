// O package pode variar, ex: com.user_service.core.config
package com.user_service.core.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1) // Garante que ele rode antes do Spring Security
public class SecretHeaderFilter implements Filter {

    @Value("${api.internal.secret}")
    private String internalApiSecret;


   /* private static final List<String> PUBLIC_API_PATHS = List.of(
            // Adicione outras rotas publicas se necessario
    );
    */

    // Lista de caminhos de monitoramento que devem ser liberados
    private static final List<String> ACTUATOR_PATHS = List.of(
            "/actuator"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // 1. Se for um endpoint do Actuator, DEIXA PASSAR.
        if (isActuatorPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        /*
        2. Se for um endpoint publico da API, DEIXA PASSAR.
        if (isPublicApiPath(path)) {
            chain.doFilter(request, response);
            return;
        }
         */

        // 3. Para todas as outras rotas (como /internal), exija o header secreto.
        String secretHeader = httpRequest.getHeader("X-Internal-Secret");
        if (internalApiSecret != null && internalApiSecret.equals(secretHeader)) {
            chain.doFilter(request, response);
        } else {
            // Se nao for publico, nao for actuator E nao tiver o header, bloqueia.
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Acesso direto nao permitido.");
        }
    }

    /*
    private boolean isPublicApiPath(String path) {
        return PUBLIC_API_PATHS.stream().anyMatch(path::startsWith);
    }
    */

    // ADICIONE ESTE NOVO METODO
    private boolean isActuatorPath(String path) {
        return ACTUATOR_PATHS.stream().anyMatch(path::startsWith);
    }
}
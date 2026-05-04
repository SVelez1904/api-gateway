package com.innovatech.api_gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1. Definimos las rutas que NO requieren Token (Públicas)
        boolean isAuth = path.contains("/api/usuarios/login") ||
                path.contains("/api/usuarios/registro");

        boolean isSwagger = path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars");

        // 2. Si es pública, dejamos pasar de inmediato
        if (isAuth || isSwagger) {
            System.out.println("GATEWAY - Ruta pública: " + path);
            return chain.filter(exchange);
        }

        // 3. RUTAS PROTEGIDAS (Aquí entra /api/proyectos y cualquier otra nueva)
        System.out.println("GATEWAY - Protegiendo ruta: " + path);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Validar existencia de la cabecera
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("GATEWAY - Bloqueado: No hay Bearer Token en " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // Validar la integridad del token
        if (!JwtUtil.validateToken(token)) {
            System.out.println("GATEWAY - Bloqueado: Token inválido para " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Si el token es válido, se envía al microservicio correspondiente (api-proyectos o api-usuarios)
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
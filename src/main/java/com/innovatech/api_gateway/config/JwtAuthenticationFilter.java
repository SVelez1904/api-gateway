package com.innovatech.api_gateway.config; // Asegúrate de que esté en el paquete del GATEWAY

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final String JWT_SECRET = "InnovatechSolutionsProjectSecretKey2026_SecureSigningKey_Minimum512BitsLength";
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 1. Validar y extraer Claims de forma reactiva
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                String rolesString = claims.get("roles", String.class);

                if (StringUtils.hasText(username)) {
                    List<SimpleGrantedAuthority> authorities = List.of();
                    if (StringUtils.hasText(rolesString)) {
                        authorities = Arrays.stream(rolesString.split(","))
                                .map(String::trim)
                                .filter(role -> !role.isEmpty())
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    }

                    // 2. Mutar la petición para INYECTAR las cabeceras que tus microservicios esperan
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Username", username)
                            .header("X-User-Roles", rolesString != null ? rolesString : "")
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    // 3. Guardar en el contexto Reactivo y continuar
                    return chain.filter(mutatedExchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                }
            } catch (Exception e) {
                // Si el token es inválido o expiró, dejamos pasar para que la seguridad reactiva del Gateway decida si rebota
                System.out.println("Gateway ❌ - Token inválido o expirado: " + e.getMessage());
            }
        }

        return chain.filter(exchange);
    }
}
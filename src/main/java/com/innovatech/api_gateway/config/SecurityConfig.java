package com.innovatech.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        System.out.println("⚠️ GATEWAY: CARGANDO CONFIGURACIÓN CENTRALIZADA");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 1. DESACTIVAR el formulario de login y la autenticación básica
                // Esto evita que Spring genere la contraseña aleatoria
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .authorizeExchange(exchange -> exchange
                        // 2. Asegurar que las rutas del microservicio de usuarios sean libres
                        .pathMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                        .pathMatchers("/api/usuarios/**").permitAll()

                        // 3. Bloquear el resto (Proyectos, etc.) para el futuro
                        .anyExchange().authenticated()
                )
                .build();
    }


}
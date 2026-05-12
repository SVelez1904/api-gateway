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
        System.out.println("⚠️ GATEWAY: CARGANDO CONFIGURACIÓN PARA SWAGGER Y USUARIOS");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .authorizeExchange(exchange -> exchange
                        // 1. RUTAS DE SWAGGER Y OPENAPI
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // 2. Rutas de la API de Usuarios
                        .pathMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                        .pathMatchers("/api/usuarios/**").permitAll()
                        .pathMatchers("/api/proyectos/**").permitAll()
                        .pathMatchers("/api/analytics/dashboard/**").permitAll()
                        // 3. El resto sigue bloqueado
                        .anyExchange().authenticated()
                )
                .build();
    }
}
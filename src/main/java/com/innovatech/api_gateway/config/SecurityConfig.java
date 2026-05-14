package com.innovatech.api_gateway.config;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .cors(Customizer.withDefaults()) 
        .authorizeExchange(exchange -> exchange
            // Usamos un matcher más agresivo para asegurar que login sea público
            .pathMatchers(HttpMethod.POST, "/api/usuarios/login", "/api/usuarios/registro").permitAll()
        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // Swagger y docs
            .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/webjars/**").permitAll()
            // El resto bloqueado
            .anyExchange().authenticated()
        )
        .build();
}
}
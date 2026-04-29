package com.Innovatech.api_gateway.controller;

import com.Innovatech.api_gateway.dto.LoginRequest;
import com.Innovatech.api_gateway.dto.LoginResponse;
import com.Innovatech.api_gateway.security.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        if ("admin".equals(request.username) && "1234".equals(request.password)) {
            String token = JwtUtil.generateToken(request.username);
            return new LoginResponse(token);
        }

        throw new RuntimeException("Credenciales inválidas");
    }
}
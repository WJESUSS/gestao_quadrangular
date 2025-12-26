package com.igreja.GestaoQuadrangular.web.controller;



import com.igreja.GestaoQuadrangular.servicce.AuthService;
import com.igreja.GestaoQuadrangular.application.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getSenha()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }
}

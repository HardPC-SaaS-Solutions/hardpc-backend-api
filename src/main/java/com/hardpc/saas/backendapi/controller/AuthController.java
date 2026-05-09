package com.hardpc.saas.backendapi.controller;

import com.hardpc.saas.backendapi.dto.AuthLoginRequestDTO;
import com.hardpc.saas.backendapi.dto.AuthResponseDTO;
import com.hardpc.saas.backendapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint público para iniciar sesión.
     * URL: POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginRequestDTO request) {
        // Delegamos toda la lógica al servicio y devolvemos un HTTP 200 OK con el DTO
        return ResponseEntity.ok(authService.login(request));
    }
}
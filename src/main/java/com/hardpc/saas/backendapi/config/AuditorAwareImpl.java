package com.hardpc.saas.backendapi.config;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        // Obtenemos el contexto de seguridad del hilo actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si no hay nadie logueado (ejemplo, el DataSeeder ejecutándose al inicio)
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SISTEMA"); // Valor por defecto
        }

        // Si hay alguien logueado, retornamos su email o username
        // Dependiendo de tu implementación de UserDetails, el principal suele ser el username
        return Optional.of(authentication.getName());
    }
}
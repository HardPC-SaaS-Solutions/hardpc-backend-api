package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.AuthLoginRequestDTO;
import com.hardpc.saas.backendapi.dto.AuthResponseDTO;
import com.hardpc.saas.backendapi.security.CustomUserDetails;
import com.hardpc.saas.backendapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Procesa la petición de login, valida credenciales y emite el JWT.
     */
    public AuthResponseDTO login(AuthLoginRequestDTO request) {

        // 1. El Jefe de Seguridad evalúa las credenciales.
        // Si la contraseña es incorrecta, esto lanzará una excepción y detendrá el proceso automáticamente (Error 403/401).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // 2. Si llegamos a esta línea, la contraseña era correcta. Extraemos los datos del usuario.
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsernameOrEmail());

        // 3. Preparamos los "Claims" (Datos extra) para inyectarlos en el token.
        // NOTA: Ajusta "getNombres()" según cómo hayas llamado al atributo en tu entidad Persona/Usuario.
        Map<String, Object> extraClaims = new HashMap<>();
        String nombreUsuario = userDetails.getUsuario().getNombres(); // O getNombreCompleto()
        String rolUsuario = userDetails.getAuthorities().iterator().next().getAuthority();

        extraClaims.put("nombres", nombreUsuario);
        extraClaims.put("rol", rolUsuario);

        // 4. La Fábrica emite el token.
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        // 5. Empaquetamos todo en el DTO de respuesta para Angular.
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .email(userDetails.getUsername())
                .nombres(nombreUsuario)
                .rol(rolUsuario)
                .build();
    }
}
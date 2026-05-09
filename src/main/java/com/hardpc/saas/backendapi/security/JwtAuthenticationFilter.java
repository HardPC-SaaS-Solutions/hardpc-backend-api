package com.hardpc.saas.backendapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * El núcleo del Guardia de Seguridad. Este método intercepta absolutamente TODAS
     * las peticiones HTTP que llegan al servidor antes de que toquen tus controladores.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer la cabecera (Header) de la petición HTTP enviada por Angular
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. ¿Tiene la cabecera el formato correcto? (Debe empezar con "Bearer ")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si no tiene token, lo dejamos pasar. SecurityConfig decidirá si lo bloquea o no más adelante.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token puro (cortamos los primeros 7 caracteres de "Bearer ")
        jwt = authHeader.substring(7);

        // 4. Extraer el email del token usando nuestro motor criptográfico
        userEmail = jwtService.extractUsername(jwt);

        // 5. Si el token tiene un email y el usuario AÚN NO está autenticado en este hilo
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Vamos a la base de datos a traer sus datos reales (Roles, Estado)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. El Juez evalúa: ¿El token es matemáticamente válido y no ha expirado?
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Si es válido, creamos la "Credencial Oficial" para Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Nulo porque ya no necesitamos la contraseña en texto plano
                        userDetails.getAuthorities()
                );

                // Le agregamos detalles extra como la IP desde donde se conecta
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Inyectamos al usuario en el Contexto de Seguridad.
                // A partir de aquí, el servidor sabe exactamente quién está haciendo la petición.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Pasarle la pelota al siguiente filtro o al Controlador final (ej. Ventas)
        filterChain.doFilter(request, response);
    }
}
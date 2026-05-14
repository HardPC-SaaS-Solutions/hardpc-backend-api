package com.hardpc.saas.backendapi.security;

import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {

        // Ejecución de consulta y desempaquetado seguro del Optional
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(identificador)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales no válidas"));

        return new CustomUserDetails(usuario);
    }
}
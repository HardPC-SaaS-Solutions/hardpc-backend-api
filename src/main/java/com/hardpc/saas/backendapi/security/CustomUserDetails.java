package com.hardpc.saas.backendapi.security;

import com.hardpc.saas.backendapi.entity.Usuario;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    // Exponemos la entidad para que el JwtService extraiga claims (como Nombres) para Angular
    @Getter
    private final Usuario usuario;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;

        // Programación Defensiva: Validamos que el rol no sea nulo antes de mapearlo
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            this.authorities = List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre().name()));
        } else {
            this.authorities = List.of(); // Lista vacía si hay un error de integridad de datos
        }
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    @NonNull
    public String getUsername() {
        // En el contexto de JWT, el identificador principal será el email
        return usuario.getEmail();
    }

    // --- Controles de Acceso ---

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        // Enlace directo: Si en la BD estado es 'false', se bloquea el acceso en el backend
        return usuario.getEstado() != null ? usuario.getEstado() : false;
    }
}
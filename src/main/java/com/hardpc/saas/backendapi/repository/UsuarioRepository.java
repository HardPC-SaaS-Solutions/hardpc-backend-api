package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Consulta optimizada para el proceso de autenticación
    @Query("SELECT u FROM Usuario u WHERE u.username = :criterio OR u.email = :criterio")
    Optional<Usuario> findByUsernameOrEmail(@Param("criterio") String criterio);

    // 2. Validaciones rápidas para futuros endpoints de registro
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
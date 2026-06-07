package com.hardpc.saas.backendapi.repository;

import com.hardpc.saas.backendapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    boolean existsByNumeroDocumento(String numeroDocumento);

    // 3. Validaciones de Unicidad (Actualización excluyendo el ID actual) ---
    boolean existsByUsernameAndIdPersonaNot(String username, Long idPersona);
    boolean existsByEmailAndIdPersonaNot(String email, Long idPersona);
    boolean existsByNumeroDocumentoAndIdPersonaNot(String numeroDocumento, Long idPersona);

    // 4. Búsqueda Paginada Dinámica usando JPQL limpio ---
    @Query("SELECT u FROM Usuario u WHERE " +
            "(:idRol IS NULL OR u.rol.idRol = :idRol) AND " + // Filtro exacto por dropdown
            "(:buscar IS NULL OR :buscar = '' OR " +       // Filtro flexible por texto
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :buscar, '%')) OR " +
            "u.numeroDocumento LIKE CONCAT('%', :buscar, '%') OR " +
            "u.telefono LIKE CONCAT('%', :buscar, '%'))")
    Page<Usuario> buscarPaginadoAvanzado(@Param("buscar") String buscar, @Param("idRol") Long idRol, Pageable pageable);

    Optional<Usuario> findByUsername(String username);
}
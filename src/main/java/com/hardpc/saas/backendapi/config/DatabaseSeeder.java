package com.hardpc.saas.backendapi.config;
import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.enums.RolNombre;
import com.hardpc.saas.backendapi.repository.RolRepository;
import com.hardpc.saas.backendapi.repository.TipoDocumentoRepository;
import com.hardpc.saas.backendapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // IMPORTANTE: Para que todo se guarde en bloque
    public void run(String... args) throws Exception {

        System.out.println("🌱 Iniciando verificación de datos maestros...");

        // 1. Sembrar Rol (Si no existe, lo crea al vuelo)
        Rol rolAdmin = rolRepository.findByNombre(RolNombre.ROLE_ADMIN)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(RolNombre.ROLE_ADMIN);
                    nuevoRol.setEstado(true); // O los campos que requiera tu entidad
                    return rolRepository.save(nuevoRol);
                });

        // 2. Sembrar Tipo de Documento (Ejemplo con DNI)
        TipoDocumento dni = tipoDocumentoRepository.findByNombre("Documento Nacional de Identidad") // O como busques
                .orElseGet(() -> {
                    TipoDocumento nuevoDoc = new TipoDocumento();
                    nuevoDoc.setNombre("Documento Nacional de Identidad");
                    nuevoDoc.setLongitudExacta(8);
                    return tipoDocumentoRepository.save(nuevoDoc);
                });

        // 3. Sembrar al Usuario Administrador (Depende de los dos anteriores)
        if (!usuarioRepository.existsByEmail("fabrizio@hardpc.com")) {

            Usuario admin = new Usuario();

            // Campos heredados de Persona
            admin.setNombres("Fabrizio Jesus");
            admin.setApellidos("Teodor Celis");
            admin.setTipoDocumento(dni); // <-- ASIGNACIÓN DE LLAVE FORÁNEA
            admin.setNumeroDocumento("61000070");

            // Campos de Usuario
            admin.setUsername("fabrizio");
            admin.setTelefono("994499515");
            admin.setEmail("fabrizio@hardpc.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEstado(true);
            admin.setRol(rolAdmin); // <-- ASIGNACIÓN DE LLAVE FORÁNEA

            usuarioRepository.save(admin);
            System.out.println("✅ Administrador Supremo creado con éxito.");
        } else {
            System.out.println("👍 La base de datos ya contiene los datos maestros iniciales.");
        }
    }
}

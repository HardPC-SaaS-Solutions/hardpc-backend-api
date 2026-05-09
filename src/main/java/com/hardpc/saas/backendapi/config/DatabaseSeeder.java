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
    @Transactional // Garantiza que si un insert falla, se haga rollback completo (Atomicidad)
    public void run(String... args) throws Exception {

        System.out.println("🌱 Inicializando catálogos y usuario maestro...");

        // 1. Validación y creación del rol por defecto
        Rol rolAdmin = rolRepository.findByNombre(RolNombre.ROLE_ADMIN)
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(RolNombre.ROLE_ADMIN);
                    nuevoRol.setEstado(true);
                    return rolRepository.save(nuevoRol);
                });

        // 2. Validación y creación del tipo de documento por defecto
        TipoDocumento dni = tipoDocumentoRepository.findByNombre("Documento Nacional de Identidad")
                .orElseGet(() -> {
                    TipoDocumento nuevoDoc = new TipoDocumento();
                    nuevoDoc.setNombre("Documento Nacional de Identidad");
                    nuevoDoc.setLongitudExacta(8);
                    return tipoDocumentoRepository.save(nuevoDoc);
                });

        // 3. Creación del administrador del sistema (Ejecución idempotente)
        if (!usuarioRepository.existsByEmail("fteodorc@hardpc.com")) {

            Usuario admin = new Usuario();

            // Datos de perfil (Persona)
            admin.setNombres("Fabrizio Jesus");
            admin.setApellidos("Teodor Celis");
            admin.setTipoDocumento(dni);
            admin.setNumeroDocumento("61000070");
            admin.setTelefono("994499515");

            // Credenciales y seguridad (Usuario)
            admin.setUsername("fteodorc");
            admin.setEmail("fteodorc@hardpc.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEstado(true);
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);
            System.out.println("✅ Administrador de HardPC creado con éxito.");
        } else {
            System.out.println("👍 Catálogos listos. El usuario maestro ya existe.");
        }
    }
}
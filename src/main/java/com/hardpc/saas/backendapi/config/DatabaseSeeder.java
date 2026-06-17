package com.hardpc.saas.backendapi.config;

import com.hardpc.saas.backendapi.entity.Cliente;
import com.hardpc.saas.backendapi.entity.Rol;
import com.hardpc.saas.backendapi.entity.TipoDocumento;
import com.hardpc.saas.backendapi.entity.Usuario;
import com.hardpc.saas.backendapi.enums.RolNombre;
import com.hardpc.saas.backendapi.enums.TipoCliente;
import com.hardpc.saas.backendapi.repository.ClienteRepository;
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
    private final ClienteRepository clienteRepository;
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

        // 2. Validación y creación/actualización adaptativa
        TipoDocumento dni = tipoDocumentoRepository.findByAbreviaturaIgnoreCase("DNI")
                .orElseGet(() -> {
                    // REGLA DE TRANSICIÓN: Si no hay "DNI", revisamos si existe el registro antiguo por Nombre
                    return tipoDocumentoRepository.findByNombreIgnoreCase("Documento Nacional de Identidad")
                            .map(docExistente -> {
                                // ¡Ajá! El registro existía pero no tenía abreviatura. Lo actualizamos aquí mismo.
                                System.out.println("🔧 Actualizando registro antiguo de TipoDocumento con su abreviatura...");
                                docExistente.setAbreviatura("DNI");
                                return tipoDocumentoRepository.save(docExistente);
                            })
                            .orElseGet(() -> {
                                // Si de verdad no existe ni por abreviatura ni por nombre, es una base de datos limpia de cero.
                                TipoDocumento nuevoDoc = new TipoDocumento();
                                nuevoDoc.setNombre("Documento Nacional de Identidad");
                                nuevoDoc.setAbreviatura("DNI");
                                nuevoDoc.setLongitudExacta(8);
                                nuevoDoc.setEstado(true);
                                return tipoDocumentoRepository.save(nuevoDoc);
                            });
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

        // 4. Creación del Cliente Genérico (Público en General) para Ventas Rápidas
        // El DNI "00000000" es el estándar tributario para ventas a clientes anónimos
        if (!clienteRepository.existsByNumeroDocumento("00000000")) {
            Cliente clienteGenerico = new Cliente();

            // Llenamos los datos como si fuera una Persona Natural
            clienteGenerico.setNombres("Público en General");
            clienteGenerico.setApellidos("");
            clienteGenerico.setTipoDocumento(dni);
            clienteGenerico.setNumeroDocumento("00000000");
            clienteGenerico.setEmail("anonimo@hardpc.com");
            clienteGenerico.setTelefono("000000000");
            clienteGenerico.setTipoCliente(TipoCliente.PERSONA_NATURAL);
            clienteGenerico.setEstado(true);

            clienteRepository.save(clienteGenerico);
            System.out.println("✅ Cliente genérico (Público en General) creado con éxito.");
        } else {
            System.out.println("👍 Catálogos listos. El cliente genérico ya existe.");
        }
    }
}
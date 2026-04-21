package com.hardpc.saas.backendapi;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService service;

    @Test
    void testGuardarUsuario() {
        Usuario u = new Usuario();
        u.setNumeroDocumento("12345678");
        u.setUsername("admin");
        u.setPassword("123");

        Usuario guardado = service.guardar(u);

        assertNotNull(guardado.getIdPersona());
    }
}
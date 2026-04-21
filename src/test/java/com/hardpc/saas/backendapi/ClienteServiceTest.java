package com.hardpc.saas.backendapi;

@SpringBootTest
public class ClienteServiceTest {

    @Autowired
    private ClienteService service;

    @Test
    void testGuardarCliente() {
        Cliente c = new Cliente();
        c.setNumeroDocumento("87654321");
        c.setNombres("Pedro");

        Cliente guardado = service.guardar(c);

        assertNotNull(guardado);
        assertNotNull(guardado.getIdPersona());
    }
}
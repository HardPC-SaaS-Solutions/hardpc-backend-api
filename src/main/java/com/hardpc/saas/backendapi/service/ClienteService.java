package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.dto.ClienteRequestDTO;
import com.hardpc.saas.backendapi.dto.ClienteResponseDTO;
import com.hardpc.saas.backendapi.enums.TipoCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    Page<ClienteResponseDTO> listarPaginado(String buscar, TipoCliente tipoCliente, Pageable pageable);
    ClienteResponseDTO buscarPorId(Long id);
    ClienteResponseDTO crear(ClienteRequestDTO dto);
    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto);
    void eliminarLogico(Long id);
    void reactivar(Long id);
}
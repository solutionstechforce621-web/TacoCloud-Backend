package com.api.Summit.API.service.interfaces;
import com.api.Summit.API.model.entities.Cliente;
import com.api.Summit.API.view.dto.ClienteDTO;
import com.api.Summit.API.view.dto.ClienteRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    Page<ClienteDTO> findAll(Pageable pageable);
    ClienteDTO findById(Long id);
    ClienteDTO save(ClienteRequestDTO clienteRequestDTO);
    ClienteDTO update(Long id, ClienteRequestDTO clienteRequestDTO);

    // Nuevos métodos con filtro por negocio
    Page<ClienteDTO> findAllByNegocioId(Long negocioId, Pageable pageable);
    ClienteDTO findByIdAndNegocioId(Long id, Long negocioId);
    ClienteDTO saveWithNegocio(ClienteRequestDTO clienteRequestDTO, Long negocioId);
    ClienteDTO updateWithNegocio(Long id, ClienteRequestDTO clienteRequestDTO, Long negocioId);
    Page<ClienteDTO> searchByNombreAndNegocioId(String nombre, Long negocioId, Pageable pageable);
    Page<ClienteDTO> findFrecuentesByNegocioId(Long negocioId, Pageable pageable);
    void deleteByIdAndNegocioId(Long id, Long negocioId);
    byte[] generateClientesReportPdf(Long negocioId, String tipoReporte);
    byte[] generateClientesExcelReport(Long negocioId, String tipoReporte);
    long countByNegocioId(Long negocioId);
    Cliente findEntityByIdAndNegocioId(Long id, Long negocioId);
}

package com.api.Summit.API.service.impl;

import com.api.Summit.API.model.entities.Cliente;
import com.api.Summit.API.model.entities.Negocio;
import com.api.Summit.API.model.repository.ClienteRepository;
import com.api.Summit.API.model.repository.NegocioRepository;
import com.api.Summit.API.reports.excel.service.ClienteExcelReportService;
import com.api.Summit.API.reports.pdf.service.ClienteReportService;
import com.api.Summit.API.service.exception.BusinessException;
import com.api.Summit.API.service.exception.ResourceNotFoundException;
import com.api.Summit.API.service.interfaces.ClienteService;
import com.api.Summit.API.view.dto.ClienteDTO;
import com.api.Summit.API.view.dto.ClienteRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final NegocioRepository negocioRepository;
    private final ClienteReportService clienteReportService;
    private final ClienteExcelReportService clienteExcelReportService;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Use findAllByNegocioId instead");
    }

    // Listar clientes por negocio
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAllByNegocioId(Long negocioId, Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findByNegocioId(negocioId, pageable);
        return clientes.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        throw new UnsupportedOperationException("Use findByIdAndNegocioId instead");
    }

    // Buscar cliente por ID y negocio (seguridad)
    @Transactional(readOnly = true)
    public ClienteDTO findByIdAndNegocioId(Long id, Long negocioId) {
        Cliente cliente = clienteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id + " para el negocio: " + negocioId));
        return convertToDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteDTO save(ClienteRequestDTO clienteRequestDTO) {
        throw new UnsupportedOperationException("Use saveWithNegocio instead");
    }

    // Crear cliente asociado a un negocio
    @Transactional
    public ClienteDTO saveWithNegocio(ClienteRequestDTO clienteRequestDTO, Long negocioId) {
        // Verificar si ya existe un cliente con el mismo nombre en este negocio
        if (clienteRepository.existsByNombreAndNegocioId(clienteRequestDTO.getNombre(), negocioId)) {
            throw new RuntimeException("Ya existe un cliente con el nombre: " + clienteRequestDTO.getNombre() + " en este negocio");
        }

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        Cliente cliente = new Cliente();
        cliente.setNombre(clienteRequestDTO.getNombre());
        cliente.setTelefono(clienteRequestDTO.getTelefono());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setDireccion(clienteRequestDTO.getDireccion());
        cliente.setFrecuente(clienteRequestDTO.getFrecuente() != null ? clienteRequestDTO.getFrecuente() : false);
        cliente.setNegocio(negocio);

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertToDTO(clienteGuardado);
    }


    @Override
    @Transactional
    public ClienteDTO update(Long id, ClienteRequestDTO clienteRequestDTO) {
        throw new UnsupportedOperationException("Use updateWithNegocio instead");
    }

    // Actualizar cliente verificando el negocio
    @Transactional
    public ClienteDTO updateWithNegocio(Long id, ClienteRequestDTO clienteRequestDTO, Long negocioId) {
        Cliente cliente = clienteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id + " para el negocio: " + negocioId));

        // Verificar si el nuevo nombre ya existe en otro cliente del mismo negocio
        if (clienteRepository.existsByNombreAndNegocioIdAndIdNot(clienteRequestDTO.getNombre(), negocioId, id)) {
            throw new RuntimeException("Ya existe otro cliente con el nombre: " + clienteRequestDTO.getNombre() + " en este negocio");
        }

        // Actualizar otros campos si están en el DTO
        cliente.setNombre(clienteRequestDTO.getNombre());
        cliente.setTelefono(clienteRequestDTO.getTelefono());
        cliente.setEmail(clienteRequestDTO.getEmail());
        cliente.setDireccion(clienteRequestDTO.getDireccion());
        cliente.setFrecuente(clienteRequestDTO.getFrecuente() != null ? clienteRequestDTO.getFrecuente() : cliente.getFrecuente());

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertToDTO(clienteActualizado);
    }

    // Buscar clientes por nombre en un negocio específico
    @Transactional(readOnly = true)
    public Page<ClienteDTO> searchByNombreAndNegocioId(String nombre, Long negocioId, Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findByNegocioIdAndNombreContainingIgnoreCase(negocioId, nombre, pageable);
        return clientes.map(this::convertToDTO);
    }

    // Obtener clientes frecuentes de un negocio
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findFrecuentesByNegocioId(Long negocioId, Pageable pageable) {
        // Implementar lógica para clientes frecuentes
        // Por ahora, devolver todos los clientes del negocio
        return findAllByNegocioId(negocioId, pageable);
    }

    // Método de conversión
    private ClienteDTO convertToDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .frecuente(cliente.getFrecuente() != null ? cliente.getFrecuente() : false)
                .build();
    }

    // Método para convertir a entidad (si necesitas uno más completo)
    private Cliente convertToEntity(ClienteDTO clienteDTO, Negocio negocio) {
        Cliente cliente = new Cliente();
        cliente.setId(clienteDTO.getId());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setNegocio(negocio);
        return cliente;
    }

    // Agregar este método en ClienteServiceImpl.java
    @Transactional
    public void deleteByIdAndNegocioId(Long id, Long negocioId) {
        // Verificar que el cliente exista y pertenezca al negocio
        Cliente cliente = clienteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id + " para el negocio: " + negocioId));

        // Eliminar el cliente
        clienteRepository.delete(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateClientesReportPdf(Long negocioId, String tipoReporte) {
        return clienteReportService.generateClientesReportPdf(negocioId, tipoReporte);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateClientesExcelReport(Long negocioId, String tipoReporte) {
        return clienteExcelReportService.generateClientesExcelReport(negocioId, tipoReporte);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByNegocioId(Long negocioId) {
        return clienteRepository.countByNegocioId(negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findEntityByIdAndNegocioId(Long id, Long negocioId) {
        return clienteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id + " para el negocio: " + negocioId));
    }

}

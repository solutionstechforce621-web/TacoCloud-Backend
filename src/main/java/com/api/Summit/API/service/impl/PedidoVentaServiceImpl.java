package com.api.Summit.API.service.impl;
import com.api.Summit.API.view.dto.*;
import com.api.Summit.API.model.entities.*;
import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.model.enums.MetodoPago;
import com.api.Summit.API.model.repository.PedidoVentaRepository;
import com.api.Summit.API.service.interfaces.ClienteService;
import com.api.Summit.API.service.interfaces.PedidoVentaService;
import com.api.Summit.API.service.interfaces.ProductoService;
import com.api.Summit.API.view.dto.PedidoRequestDTO;
import com.api.Summit.API.view.dto.PedidoVentaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoVentaServiceImpl implements PedidoVentaService {

    private final PedidoVentaRepository pedidoVentaRepository;
    private final NegocioService negocioService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    @Override
    @Transactional
    public PedidoVentaDTO createPedido(PedidoRequestDTO pedidoRequestDTO, Long negocioId) {
        // Validar negocio
        Negocio negocio = negocioService.findEntityById(negocioId);

        // Validar cliente si se proporciona
        Cliente cliente = null;
        if (pedidoRequestDTO.getClienteId() != null) {
            cliente = clienteService.findEntityByIdAndNegocioId(pedidoRequestDTO.getClienteId(), negocioId);
        }

        // Crear pedido
        PedidoVenta pedido = PedidoVenta.builder()
                .tipoPedido(pedidoRequestDTO.getTipoPedido())
                .estado(EstadoPedido.PENDIENTE)
                .observaciones(pedidoRequestDTO.getObservaciones())
                .nombreCliente(pedidoRequestDTO.getNombreCliente())
                .negocio(negocio)
                .cliente(cliente)
                .total(BigDecimal.ZERO)
                .build();

        // Generar tickets
        pedido.setTicketCocina(generarTicketCocina(negocioId));
        pedido.setTicketCliente(generarTicketCliente(negocioId));

        // Procesar detalles
        procesarDetallesPedido(pedido, pedidoRequestDTO.getDetalles(), negocioId);

        // Calcular total
        calcularTotalPedido(pedido);

        // Guardar pedido
        PedidoVenta pedidoGuardado = pedidoVentaRepository.save(pedido);

        return PedidoVentaDTO.fromEntity(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoVentaDTO> findAllByNegocioId(Long negocioId, Pageable pageable) {
        return pedidoVentaRepository.findByNegocioId(negocioId, pageable)
                .map(PedidoVentaDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoVentaDTO> findByEstadoAndNegocioId(EstadoPedido estado, Long negocioId, Pageable pageable) {
        return pedidoVentaRepository.findByEstadoAndNegocioId(estado, negocioId, pageable)
                .map(PedidoVentaDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoVentaDTO> findByEstadosAndNegocioId(List<EstadoPedido> estados, Long negocioId, Pageable pageable) {
        return pedidoVentaRepository.findByEstadosAndNegocioId(estados, negocioId, pageable)
                .map(PedidoVentaDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoVentaDTO findByIdAndNegocioId(Long id, Long negocioId) {
        PedidoVenta pedido = pedidoVentaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return PedidoVentaDTO.fromEntity(pedido);
    }

    @Override
    @Transactional
    public PedidoVentaDTO updatePedido(Long id, PedidoRequestDTO pedidoRequestDTO, Long negocioId) {
        PedidoVenta pedido = pedidoVentaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar que el pedido se puede modificar
        if (pedido.getEstado() == EstadoPedido.PAGADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("No se puede modificar un pedido " + pedido.getEstado());
        }

        // Actualizar datos básicos
        pedido.setTipoPedido(pedidoRequestDTO.getTipoPedido());
        pedido.setObservaciones(pedidoRequestDTO.getObservaciones());
        pedido.setNombreCliente(pedidoRequestDTO.getNombreCliente());

        // Actualizar cliente si se proporciona
        if (pedidoRequestDTO.getClienteId() != null) {
            Cliente cliente = clienteService.findEntityByIdAndNegocioId(pedidoRequestDTO.getClienteId(), negocioId);
            pedido.setCliente(cliente);
        } else {
            pedido.setCliente(null);
        }

        // Limpiar detalles existentes
        pedido.getDetalles().clear();

        // Procesar nuevos detalles
        procesarDetallesPedido(pedido, pedidoRequestDTO.getDetalles(), negocioId);

        // Recalcular total
        calcularTotalPedido(pedido);

        PedidoVenta pedidoActualizado = pedidoVentaRepository.save(pedido);
        return PedidoVentaDTO.fromEntity(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoVentaDTO marcarComoPagado(Long id, Long negocioId, String metodoPago) {
        PedidoVenta pedido = pedidoVentaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new RuntimeException("El pedido ya está pagado");
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("No se puede pagar un pedido cancelado");
        }

        // Crear pago
        Pago pago = Pago.builder()
                .pedidoVenta(pedido)
                .metodoPago(MetodoPago.valueOf(metodoPago))
                .monto(pedido.getTotal())
                .fechaPago(LocalDateTime.now())
                .build();

        pedido.setPago(pago);
        pedido.setEstado(EstadoPedido.PAGADO);

        PedidoVenta pedidoPagado = pedidoVentaRepository.save(pedido);
        return PedidoVentaDTO.fromEntity(pedidoPagado);
    }

    @Override
    @Transactional
    public PedidoVentaDTO cambiarEstado(Long id, Long negocioId, EstadoPedido nuevoEstado) {
        PedidoVenta pedido = pedidoVentaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar transición de estado
        validarTransicionEstado(pedido.getEstado(), nuevoEstado);

        pedido.setEstado(nuevoEstado);

        PedidoVenta pedidoActualizado = pedidoVentaRepository.save(pedido);
        return PedidoVentaDTO.fromEntity(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoVentaDTO cancelarPedido(Long id, Long negocioId) {
        return cambiarEstado(id, negocioId, EstadoPedido.CANCELADO);
    }

    @Override
    @Transactional
    public void deletePedido(Long id, Long negocioId) {
        PedidoVenta pedido = pedidoVentaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Solo se puede eliminar si no está pagado o cancelado
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new RuntimeException("No se puede eliminar un pedido pagado");
        }

        pedidoVentaRepository.delete(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByNegocioId(Long negocioId) {
        return pedidoVentaRepository.countByNegocioId(negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByEstadoAndNegocioId(EstadoPedido estado, Long negocioId) {
        return pedidoVentaRepository.countByEstadoAndNegocioId(estado, negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoVentaDTO> findPedidosCocinaByNegocioId(Long negocioId) {
        return pedidoVentaRepository.findPedidosCocinaByNegocioId(negocioId)
                .stream()
                .map(PedidoVentaDTO::fromEntity)
                .toList();
    }

    // Métodos auxiliares privados
    private void procesarDetallesPedido(PedidoVenta pedido, List<DetallePedidoRequestDTO> detallesRequest, Long negocioId) {
        if (detallesRequest == null || detallesRequest.isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un producto");
        }

        for (DetallePedidoRequestDTO detalleRequest : detallesRequest) {
            Producto producto = productoService.findEntityByIdAndNegocioId(detalleRequest.getProductoId(), negocioId);

            // Convertir double a BigDecimal
            BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecioUnitario());
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));

            DetallePedidoVenta detalle = DetallePedidoVenta.builder()
                    .producto(producto)
                    .cantidad(detalleRequest.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .nota(detalleRequest.getNota())
                    .build();
            pedido.addDetalle(detalle);
        }
    }

    private void calcularTotalPedido(PedidoVenta pedido) {
        BigDecimal total = pedido.getDetalles().stream()
                .map(DetallePedidoVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);
    }

    private String generarTicketCocina(Long negocioId) {
        Optional<String> ultimoTicket = pedidoVentaRepository.findLastTicketCocinaByNegocioId(negocioId);
        int siguienteNumero = ultimoTicket.map(ticket -> Integer.parseInt(ticket.substring(1)) + 1)
                .orElse(1);
        return "C" + String.format("%04d", siguienteNumero);
    }

    private String generarTicketCliente(Long negocioId) {
        Optional<String> ultimoTicket = pedidoVentaRepository.findLastTicketClienteByNegocioId(negocioId);
        int siguienteNumero = ultimoTicket.map(ticket -> Integer.parseInt(ticket.substring(1)) + 1)
                .orElse(1);
        return "T" + String.format("%04d", siguienteNumero);
    }

    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        // Lógica de validación de transiciones de estado
        if (estadoActual == EstadoPedido.CANCELADO || estadoActual == EstadoPedido.PAGADO) {
            throw new RuntimeException("No se puede modificar un pedido " + estadoActual);
        }
    }
}

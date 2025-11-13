package com.api.Summit.API.service.interfaces;

import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.view.dto.PedidoRequestDTO;
import com.api.Summit.API.view.dto.PedidoVentaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PedidoVentaService {

    // ➕ Crear pedido
    PedidoVentaDTO createPedido(PedidoRequestDTO pedidoRequestDTO, Long negocioId);

    // 📋 Listar pedidos por negocio
    Page<PedidoVentaDTO> findAllByNegocioId(Long negocioId, Pageable pageable);

    // 📋 Listar pedidos por estado y negocio
    Page<PedidoVentaDTO> findByEstadoAndNegocioId(EstadoPedido estado, Long negocioId, Pageable pageable);

    // 📋 Listar pedidos por múltiples estados y negocio
    Page<PedidoVentaDTO> findByEstadosAndNegocioId(List<EstadoPedido> estados, Long negocioId, Pageable pageable);

    // 🔍 Obtener pedido por ID y negocio
    PedidoVentaDTO findByIdAndNegocioId(Long id, Long negocioId);

    // ✏️ Actualizar pedido
    PedidoVentaDTO updatePedido(Long id, PedidoRequestDTO pedidoRequestDTO, Long negocioId);

    // 💵 Marcar como pagado
    PedidoVentaDTO marcarComoPagado(Long id, Long negocioId, String metodoPago);

    // 🔄 Cambiar estado del pedido
    PedidoVentaDTO cambiarEstado(Long id, Long negocioId, EstadoPedido nuevoEstado);

    // ❌ Cancelar pedido
    PedidoVentaDTO cancelarPedido(Long id, Long negocioId);

    // 🗑️ Eliminar pedido (solo si no ha sido pagado)
    void deletePedido(Long id, Long negocioId);

    // 📊 Estadísticas
    long countByNegocioId(Long negocioId);
    long countByEstadoAndNegocioId(EstadoPedido estado, Long negocioId);

    // 🍳 Pedidos para cocina
    List<PedidoVentaDTO> findPedidosCocinaByNegocioId(Long negocioId);
}

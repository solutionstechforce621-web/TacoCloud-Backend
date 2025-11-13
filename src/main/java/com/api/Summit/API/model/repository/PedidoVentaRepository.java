package com.api.Summit.API.model.repository;

import com.api.Summit.API.model.entities.PedidoVenta;
import com.api.Summit.API.model.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoVentaRepository extends JpaRepository<PedidoVenta, Long> {
    // Buscar pedidos por negocio
    Page<PedidoVenta> findByNegocioId(Long negocioId, Pageable pageable);

    // Buscar pedido por ID y negocio (para seguridad)
    Optional<PedidoVenta> findByIdAndNegocioId(Long id, Long negocioId);

    // Buscar pedidos por estado y negocio
    Page<PedidoVenta> findByEstadoAndNegocioId(EstadoPedido estado, Long negocioId, Pageable pageable);

    // Buscar pedidos por múltiples estados y negocio
    @Query("SELECT p FROM PedidoVenta p WHERE p.estado IN :estados AND p.negocio.id = :negocioId")
    Page<PedidoVenta> findByEstadosAndNegocioId(@Param("estados") List<EstadoPedido> estados,
                                                @Param("negocioId") Long negocioId,
                                                Pageable pageable);

    // Buscar pedidos por cliente y negocio
    Page<PedidoVenta> findByClienteIdAndNegocioId(Long clienteId, Long negocioId, Pageable pageable);

    // Contar pedidos por estado y negocio
    long countByEstadoAndNegocioId(EstadoPedido estado, Long negocioId);

    // Contar total de pedidos por negocio
    long countByNegocioId(Long negocioId);

    // Verificar si existe un pedido con ticket de cocina en el mismo negocio
    boolean existsByTicketCocinaAndNegocioId(String ticketCocina, Long negocioId);

    // Buscar pedidos pendientes o en preparación (para cocina)
    @Query("SELECT p FROM PedidoVenta p WHERE p.estado IN (com.api.Summit.API.model.enums.EstadoPedido.PENDIENTE, com.api.Summit.API.model.enums.EstadoPedido.EN_PREPARACION) AND p.negocio.id = :negocioId ORDER BY p.createdAt ASC")
    List<PedidoVenta> findPedidosCocinaByNegocioId(@Param("negocioId") Long negocioId);

    // Obtener el último ticket de cocina por negocio
    @Query("SELECT p.ticketCocina FROM PedidoVenta p WHERE p.negocio.id = :negocioId AND p.ticketCocina IS NOT NULL ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLastTicketCocinaByNegocioId(@Param("negocioId") Long negocioId);

    // Obtener el último ticket de cliente por negocio
    @Query("SELECT p.ticketCliente FROM PedidoVenta p WHERE p.negocio.id = :negocioId AND p.ticketCliente IS NOT NULL ORDER BY p.id DESC LIMIT 1")
    Optional<String> findLastTicketClienteByNegocioId(@Param("negocioId") Long negocioId);
}
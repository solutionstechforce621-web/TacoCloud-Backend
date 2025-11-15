package com.api.Summit.API.view.dto;

import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.model.enums.TipoPedido;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoVentaDTO {
    private Long id;
    private TipoPedido tipoPedido;
    private EstadoPedido estado;
    private BigDecimal total;
    private String observaciones;
    private String ticketCocina;
    private String ticketCliente;
    private Long negocioId;
    private Long clienteId;
    private String clienteNombre;
    private String nombreCliente;
    private List<DetallePedidoVentaDTO> detalles;
    private PagoDTO pago;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PedidoVentaDTO fromEntity(com.api.Summit.API.model.entities.PedidoVenta pedido) {
        return PedidoVentaDTO.builder()
                .id(pedido.getId())
                .tipoPedido(pedido.getTipoPedido())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .observaciones(pedido.getObservaciones())
                .ticketCocina(pedido.getTicketCocina())
                .ticketCliente(pedido.getTicketCliente())
                .negocioId(pedido.getNegocio().getId())
                .clienteId(pedido.getCliente() != null ? pedido.getCliente().getId() : null)
                .clienteNombre(pedido.getCliente() != null ? pedido.getCliente().getNombre() : null)
                .nombreCliente(pedido.getNombreCliente())
                .detalles(pedido.getDetalles() != null ?
                        pedido.getDetalles().stream().map(DetallePedidoVentaDTO::fromEntity).toList() : null)
                .pago(pedido.getPago() != null ? PagoDTO.fromEntity(pedido.getPago()) : null)
                .createdAt(pedido.getCreatedAt())
                .updatedAt(pedido.getUpdatedAt())
                .build();
    }
}

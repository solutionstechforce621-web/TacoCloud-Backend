package com.api.Summit.API.view.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoVentaDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String nota;

    public static DetallePedidoVentaDTO fromEntity(com.api.Summit.API.model.entities.DetallePedidoVenta detalle) {
        return DetallePedidoVentaDTO.builder()
                .id(detalle.getId())
                .productoId(detalle.getProducto().getId())
                .productoNombre(detalle.getProducto().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .nota(detalle.getNota())
                .build();
    }
}

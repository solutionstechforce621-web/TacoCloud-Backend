package com.api.Summit.API.view.dto;

import com.api.Summit.API.model.entities.Negocio;
import com.api.Summit.API.model.entities.PedidoVenta;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NegocioDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String domicilio;
    private String rfc;
    private String codigoPostal;
    private String correo;
    private String telefono;

    public static NegocioDTO fromNegocio(Negocio negocio) {
        return NegocioDTO.builder()
                .id(negocio.getId())
                .nombre(negocio.getNombre())
                .descripcion(negocio.getDescripcion())
                .domicilio(negocio.getDomicilio())
                .rfc(negocio.getRfc())
                .codigoPostal(negocio.getCodigoPostal())
                .correo(negocio.getCorreo())
                .telefono(negocio.getTelefono())
                .build();
    }

    public static Negocio toEntity(NegocioDTO negocioDTO) {
        return Negocio.builder()
                .id(negocioDTO.getId())
                .nombre(negocioDTO.getNombre())
                .descripcion(negocioDTO.getDescripcion())
                .domicilio(negocioDTO.getDomicilio())
                .rfc(negocioDTO.getRfc())
                .codigoPostal(negocioDTO.getCodigoPostal())
                .correo(negocioDTO.getCorreo())
                .telefono(negocioDTO.getTelefono())
                .build();
    }

    public static PedidoVentaDTO fromEntity(PedidoVenta pedido) {
        return PedidoVentaDTO.builder()
                .id(pedido.getId())
                .tipoPedido(pedido.getTipoPedido())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .observaciones(pedido.getObservaciones())
                .ticketCocina(pedido.getTicketCocina())
                .ticketCliente(pedido.getTicketCliente())
                .negocio(NegocioDTO.fromNegocio(pedido.getNegocio())) // ← AGREGADO
                .clienteId(pedido.getCliente() != null ? pedido.getCliente().getId() : null)
                .clienteNombre(pedido.getCliente() != null ? pedido.getCliente().getNombre() : null)
                .nombreCliente(pedido.getNombreCliente())
                .detalles(pedido.getDetalles().stream().map(DetallePedidoVentaDTO::fromEntity).toList())
                .pago(pedido.getPago() != null ? PagoDTO.fromEntity(pedido.getPago()) : null)
                .createdAt(pedido.getCreatedAt())
                .updatedAt(pedido.getUpdatedAt())
                .build();
    }
}

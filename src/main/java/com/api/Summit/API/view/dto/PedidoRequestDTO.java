package com.api.Summit.API.view.dto;

import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.model.enums.TipoPedido;
import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {
    private TipoPedido tipoPedido;
    private EstadoPedido estado;
    private String observaciones;
    private Long clienteId;
    private String nombreCliente;
    private List<DetallePedidoRequestDTO> detalles;
}

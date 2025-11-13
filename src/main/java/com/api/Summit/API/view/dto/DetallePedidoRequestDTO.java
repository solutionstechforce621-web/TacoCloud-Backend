package com.api.Summit.API.view.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoRequestDTO {
    private Long productoId;
    private Integer cantidad;
    private String nota;
}

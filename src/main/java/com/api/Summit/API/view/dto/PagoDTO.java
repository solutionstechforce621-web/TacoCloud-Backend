package com.api.Summit.API.view.dto;

import com.api.Summit.API.model.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {
    private Long id;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;

    public static PagoDTO fromEntity(com.api.Summit.API.model.entities.Pago pago) {
        return PagoDTO.builder()
                .id(pago.getId())
                .metodoPago(pago.getMetodoPago())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}

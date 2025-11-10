package com.api.Summit.API.view.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioReportDataDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private int cantidad;
    private int cantidadMinima;
    private int cantidadMaxima;
    private String estadoStock;
    private double precioUnitario;
    private double valorTotal;
}

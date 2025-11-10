package com.api.Summit.API.view.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioReportDTO {
    private String titulo;
    private String fechaGeneracion;
    private String periodo;
    private String negocioNombre;
    private List<InventarioReportDataDTO> productos;
    private int totalProductos;
    private int productosBajoStock;
    private int productosSobreStock;
    private int productosStockNormal;
    private double valorTotalInventario;
}

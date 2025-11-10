package com.api.Summit.API.reports.pdf.service;

import com.api.Summit.API.model.entities.Inventario;
import com.api.Summit.API.model.entities.Negocio;
import com.api.Summit.API.model.repository.InventarioRepository;
import com.api.Summit.API.model.repository.NegocioRepository;
import com.api.Summit.API.service.interfaces.InventarioService;
import com.api.Summit.API.view.dto.InventarioDTO;
import com.api.Summit.API.view.dto.InventarioReportDTO;
import com.api.Summit.API.view.dto.InventarioReportDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioReportService {

    private final InventarioRepository inventarioRepository;
    private final NegocioRepository negocioRepository;
    private final InventarioService inventarioService;
    private final PdfReportService pdfReportService;

    public byte[] generateInventarioReportPdf(Long negocioId, String tipoReporte, String periodo) {
        try {
            List<Inventario> inventarios;
            String titulo = "";
            String periodoTexto = "";

            // Obtener información del negocio
            Negocio negocio = negocioRepository.findById(negocioId)
                    .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

            switch (tipoReporte.toLowerCase()) {
                case "bajo-stock":
                    Page<Inventario> bajoStockPage = inventarioRepository.findByCantidadLessThanCantidadMinimaAndNegocioId(negocioId, Pageable.unpaged());
                    inventarios = bajoStockPage.getContent();
                    titulo = "PRODUCTOS CON STOCK BAJO";
                    break;
                case "sobre-stock":
                    Page<Inventario> sobreStockPage = inventarioRepository.findByCantidadGreaterThanCantidadMaximaAndNegocioId(negocioId, Pageable.unpaged());
                    inventarios = sobreStockPage.getContent();
                    titulo = "PRODUCTOS CON SOBRE STOCK";
                    break;
                case "diario":
                    Page<Inventario> diarioPage = inventarioRepository.findByNegocioId(negocioId, Pageable.unpaged());
                    inventarios = diarioPage.getContent();
                    titulo = "REPORTE DIARIO DE INVENTARIO";
                    periodoTexto = "Día: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    break;
                case "semanal":
                    Page<Inventario> semanalPage = inventarioRepository.findByNegocioId(negocioId, Pageable.unpaged());
                    inventarios = semanalPage.getContent();
                    titulo = "REPORTE SEMANAL DE INVENTARIO";
                    periodoTexto = "Semana: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    break;
                case "mensual":
                    Page<Inventario> mensualPage = inventarioRepository.findByNegocioId(negocioId, Pageable.unpaged());
                    inventarios = mensualPage.getContent();
                    titulo = "REPORTE MENSUAL DE INVENTARIO";
                    periodoTexto = "Mes: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                    break;
                case "completo":
                default:
                    Page<Inventario> completoPage = inventarioRepository.findByNegocioId(negocioId, Pageable.unpaged());
                    inventarios = completoPage.getContent();
                    titulo = "REPORTE COMPLETO DE INVENTARIO";
                    break;
            }

            List<InventarioReportDataDTO> productosDTO = inventarios.stream()
                    .map(this::convertToReportDataDTO)
                    .collect(Collectors.toList());

            // Calcular métricas
            int productosBajoStock = (int) productosDTO.stream()
                    .filter(p -> "CRÍTICO".equals(p.getEstadoStock()) || "BAJO".equals(p.getEstadoStock()))
                    .count();

            int productosSobreStock = (int) productosDTO.stream()
                    .filter(p -> "SOBRE STOCK".equals(p.getEstadoStock()))
                    .count();

            int productosStockNormal = productosDTO.size() - productosBajoStock - productosSobreStock;

            double valorTotalInventario = productosDTO.stream()
                    .mapToDouble(InventarioReportDataDTO::getValorTotal)
                    .sum();

            InventarioReportDTO reportDTO = InventarioReportDTO.builder()
                    .titulo(titulo)
                    .fechaGeneracion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .periodo(periodoTexto)
                    .negocioNombre(negocio.getNombre())
                    .productos(productosDTO)
                    .totalProductos(productosDTO.size())
                    .productosBajoStock(productosBajoStock)
                    .productosSobreStock(productosSobreStock)
                    .productosStockNormal(productosStockNormal)
                    .valorTotalInventario(valorTotalInventario)
                    .build();

            return pdfReportService.generateInventarioReport(reportDTO, tipoReporte);

        } catch (Exception e) {
            log.error("Error generando reporte de inventario", e);
            throw new RuntimeException("Error al generar reporte: " + e.getMessage());
        }
    }

    private InventarioReportDataDTO convertToReportDataDTO(Inventario inventario) {
        String estadoStock = calcularEstadoStock(inventario);
        double valorTotal = inventario.getCantidad() * inventario.getProducto().getPrecioUnitario();

        // Obtener categorías como string separado por comas
        String categorias = inventario.getProducto().getCategorias().stream()
                .map(categoria -> categoria.getNombre())
                .collect(Collectors.joining(", "));

        // Si no tiene categorías, poner "Sin categoría"
        if (categorias.isEmpty()) {
            categorias = "Sin categoría";
        }

        return InventarioReportDataDTO.builder()
                .id(inventario.getId())
                .nombre(inventario.getProducto().getNombre())
                .categoria(categorias) // Ahora es un string con todas las categorías
                .cantidad(inventario.getCantidad())
                .cantidadMinima(inventario.getCantidadMinima())
                .cantidadMaxima(inventario.getCantidadMaxima())
                .estadoStock(estadoStock)
                .precioUnitario(inventario.getProducto().getPrecioUnitario())
                .valorTotal(valorTotal)
                .build();
    }

    private String calcularEstadoStock(Inventario inventario) {
        if (inventario.getCantidad() <= 0) {
            return "AGOTADO";
        } else if (inventario.getCantidad() <= inventario.getCantidadMinima() * 0.3) {
            return "CRÍTICO";
        } else if (inventario.getCantidad() < inventario.getCantidadMinima()) {
            return "BAJO";
        } else if (inventario.getCantidad() > inventario.getCantidadMaxima()) {
            return "SOBRE STOCK";
        } else {
            return "NORMAL";
        }
    }
}
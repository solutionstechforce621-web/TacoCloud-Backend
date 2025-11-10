package com.api.Summit.API.reports.pdf.service;

import com.api.Summit.API.view.dto.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfReportService {
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

    public byte[] generateClientesReport(ClienteReportDTO reportDTO) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Crear tabla para el encabezado (imagen y texto)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            // Celda para la imagen
            try {
                Image logo = Image.getInstance(new ClassPathResource("static/reporte.png").getURL());
                logo.scaleToFit(80, 80);
                PdfPCell imageCell = new PdfPCell(logo, true);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                imageCell.setPadding(5);
                headerTable.addCell(imageCell);
            } catch (IOException e) {
                log.warn("No se pudo cargar la imagen del logo, usando celda vacía");
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Celda para la información del reporte
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.setPadding(5);

            Paragraph title = new Paragraph("REPORTE DE CLIENTES", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);

            Paragraph date = new Paragraph("Fecha: " + reportDTO.getFechaGeneracion(), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);

            Paragraph subtitle = new Paragraph(reportDTO.getTitulo(), NORMAL_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);

            // Agregar información del negocio
            Paragraph negocioInfo = new Paragraph("Negocio: " + reportDTO.getNegocioNombre(), SMALL_FONT);
            negocioInfo.setAlignment(Element.ALIGN_CENTER);

            infoCell.addElement(title);
            infoCell.addElement(date);
            infoCell.addElement(subtitle);
            infoCell.addElement(negocioInfo); // Agregar esta línea
            headerTable.addCell(infoCell);

            document.add(headerTable);
            document.add(new Paragraph(" ")); // Espacio en blanco

            // Crear tabla para los datos de clientes
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 3, 3, 2});

            // Encabezados de la tabla con color azul
            addTableHeader(table, "ID");
            addTableHeader(table, "NOMBRE");
            addTableHeader(table, "TELÉFONO");
            addTableHeader(table, "EMAIL");
            addTableHeader(table, "DIRECCIÓN");
            addTableHeader(table, "FRECUENTE");

            // Datos de los clientes
            for (var cliente : reportDTO.getClientes()) {
                addTableRow(table, cliente.getId().toString());
                addTableRow(table, cliente.getNombre());
                addTableRow(table, cliente.getTelefono() != null ? cliente.getTelefono() : "");
                addTableRow(table, cliente.getEmail() != null ? cliente.getEmail() : "");
                addTableRow(table, cliente.getDireccion() != null ? cliente.getDireccion() : "");
                addTableRow(table, cliente.isFrecuente() ? "SÍ" : "NO");
            }

            document.add(table);

            // Agregar resumen al final
            document.add(new Paragraph(" "));
            Paragraph summary = new Paragraph(
                    String.format("Total de clientes: %d | Clientes frecuentes: %d",
                            reportDTO.getTotalClientes(),
                            reportDTO.getTotalFrecuentes()),
                    NORMAL_FONT
            );
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte PDF", e);
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
        }
    }


    private void addTableHeader(PdfPTable table, String header) {
        PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(51, 122, 183)); // Azul
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content, NORMAL_FONT));
        cell.setPadding(5);
        table.addCell(cell);
    }

    public byte[] generateCategoriasReport(CategoriaReportDTO reportDTO, String tipoReporte) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Crear tabla para el encabezado (imagen y texto)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            // Celda para la imagen
            try {
                Image logo = Image.getInstance(new ClassPathResource("static/logo.png").getURL());
                logo.scaleToFit(80, 80);
                PdfPCell imageCell = new PdfPCell(logo, true);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                imageCell.setPadding(5);
                headerTable.addCell(imageCell);
            } catch (IOException e) {
                log.warn("No se pudo cargar la imagen del logo, usando celda vacía");
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Celda para la información del reporte
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.setPadding(5);

            Paragraph title = new Paragraph("REPORTE DE CATEGORÍAS", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);

            Paragraph date = new Paragraph("Fecha: " + reportDTO.getFechaGeneracion(), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);

            Paragraph subtitle = new Paragraph(reportDTO.getTitulo(), NORMAL_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);

            Paragraph negocioInfo = new Paragraph("Negocio: " + reportDTO.getNegocioNombre(), SMALL_FONT);
            negocioInfo.setAlignment(Element.ALIGN_CENTER);

            infoCell.addElement(title);
            infoCell.addElement(date);
            infoCell.addElement(subtitle);
            infoCell.addElement(negocioInfo);
            headerTable.addCell(infoCell);

            document.add(headerTable);
            document.add(new Paragraph(" ")); // Espacio en blanco

            if ("detallado".equalsIgnoreCase(tipoReporte)) {
                generateDetalladoReport(document, reportDTO);
            } else {
                generateResumenReport(document, reportDTO);
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte PDF de categorías", e);
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    private void generateDetalladoReport(Document document, CategoriaReportDTO reportDTO) throws DocumentException {
        // Crear tabla para los datos de categorías (reporte detallado)
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 3, 2, 4});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "DESCRIPCIÓN");
        addTableHeader(table, "N° PRODUCTOS");
        addTableHeader(table, "PRODUCTOS PRINCIPALES");

        // Datos de las categorías
        for (var categoria : reportDTO.getCategorias()) {
            addTableRow(table, categoria.getId().toString());
            addTableRow(table, categoria.getNombre());
            addTableRow(table, categoria.getDescripcion());
            addTableRow(table, String.valueOf(categoria.getCantidadProductos()));
            addTableRow(table, categoria.getProductosPrincipales());
        }

        document.add(table);

        // Agregar resumen al final
        addResumenSection(document, reportDTO);
    }

    private void generateResumenReport(Document document, CategoriaReportDTO reportDTO) throws DocumentException {
        // Tabla más simple para reporte resumen
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "N° PRODUCTOS");

        // Datos de las categorías
        for (var categoria : reportDTO.getCategorias()) {
            addTableRow(table, categoria.getId().toString());
            addTableRow(table, categoria.getNombre());
            addTableRow(table, String.valueOf(categoria.getCantidadProductos()));
        }

        document.add(table);

        // Agregar resumen al final
        addResumenSection(document, reportDTO);
    }

    private void addResumenSection(Document document, CategoriaReportDTO reportDTO) throws DocumentException {
        document.add(new Paragraph(" "));

        // Crear tabla para el resumen
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setWidths(new float[]{2, 1});

        // Filas del resumen
        addSummaryRow(summaryTable, "Total de categorías:", String.valueOf(reportDTO.getTotalCategorias()));
        addSummaryRow(summaryTable, "Total de productos:", String.valueOf(reportDTO.getTotalProductos()));

        if (reportDTO.getTotalCategorias() > 0) {
            double promedioProductos = (double) reportDTO.getTotalProductos() / reportDTO.getTotalCategorias();
            addSummaryRow(summaryTable, "Promedio productos/categoría:", String.format("%.1f", promedioProductos));
        }

        document.add(summaryTable);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, NORMAL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    public byte[] generateProductosReport(ProductoReportDTO reportDTO, String tipoReporte) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Crear tabla para el encabezado (imagen y texto)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3});

            // Celda para la imagen
            try {
                Image logo = Image.getInstance(new ClassPathResource("static/reporte.png").getURL());
                logo.scaleToFit(80, 80);
                PdfPCell imageCell = new PdfPCell(logo, true);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                imageCell.setPadding(5);
                headerTable.addCell(imageCell);
            } catch (IOException e) {
                log.warn("No se pudo cargar la imagen del logo, usando celda vacía");
                PdfPCell emptyCell = new PdfPCell();
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Celda para la información del reporte
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.setPadding(5);

            Paragraph title = new Paragraph("REPORTE DE PRODUCTOS", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);

            Paragraph date = new Paragraph("Fecha: " + reportDTO.getFechaGeneracion(), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_CENTER);

            Paragraph subtitle = new Paragraph(reportDTO.getTitulo(), NORMAL_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);

            Paragraph negocioInfo = new Paragraph("Negocio: " + reportDTO.getNegocioNombre(), SMALL_FONT);
            negocioInfo.setAlignment(Element.ALIGN_CENTER);

            infoCell.addElement(title);
            infoCell.addElement(date);
            infoCell.addElement(subtitle);
            infoCell.addElement(negocioInfo);
            headerTable.addCell(infoCell);

            document.add(headerTable);
            document.add(new Paragraph(" ")); // Espacio en blanco

            switch (tipoReporte.toLowerCase()) {
                case "inventario":
                    generateInventarioReport(document, reportDTO);
                    break;
                case "rentabilidad":
                    generateRentabilidadReport(document, reportDTO);
                    break;
                case "resumen":
                    generateResumenProductosReport(document, reportDTO);
                    break;
                default:
                    generateDefaultProductosReport(document, reportDTO);
                    break;
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte PDF de productos", e);
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    private void generateInventarioReport(Document document, ProductoReportDTO reportDTO) throws DocumentException {
        // Tabla para reporte de inventario
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 3});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "PRECIO");
        addTableHeader(table, "COSTO");
        addTableHeader(table, "MARGEN");
        addTableHeader(table, "CATEGORÍAS");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            addTableRow(table, producto.getId().toString());
            addTableRow(table, producto.getNombre());
            addTableRow(table, String.format("$%.2f", producto.getPrecioUnitario()));
            addTableRow(table, String.format("$%.2f", producto.getCosto()));
            addTableRow(table, String.format("$%.2f", producto.getMargenGanancia()));
            addTableRow(table, producto.getCategorias());
        }

        document.add(table);
        addResumenProductosSection(document, reportDTO);
    }

    private void generateRentabilidadReport(Document document, ProductoReportDTO reportDTO) throws DocumentException {
        // Tabla para reporte de rentabilidad
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 3, 2});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "PRECIO");
        addTableHeader(table, "COSTO");
        addTableHeader(table, "MARGEN $");
        addTableHeader(table, "MARGEN %");
        addTableHeader(table, "CATEGORÍAS");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            addTableRow(table, producto.getId().toString());
            addTableRow(table, producto.getNombre());
            addTableRow(table, String.format("$%.2f", producto.getPrecioUnitario()));
            addTableRow(table, String.format("$%.2f", producto.getCosto()));
            addTableRow(table, String.format("$%.2f", producto.getMargenGanancia()));
            addTableRow(table, String.format("%.1f%%", producto.getPorcentajeMargen()));
            addTableRow(table, producto.getCategorias());
        }

        document.add(table);
        addResumenProductosSection(document, reportDTO);
    }

    private void generateResumenProductosReport(Document document, ProductoReportDTO reportDTO) throws DocumentException {
        // Tabla simplificada para reporte resumen
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "PRECIO");
        addTableHeader(table, "CATEGORÍAS");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            addTableRow(table, producto.getId().toString());
            addTableRow(table, producto.getNombre());
            addTableRow(table, String.format("$%.2f", producto.getPrecioUnitario()));
            addTableRow(table, String.valueOf(producto.getCantidadCategorias()));
        }

        document.add(table);
        addResumenProductosSection(document, reportDTO);
    }

    private void generateDefaultProductosReport(Document document, ProductoReportDTO reportDTO) throws DocumentException {
        // Tabla por defecto
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 3});

        // Encabezados de la tabla con color azul
        addTableHeader(table, "ID");
        addTableHeader(table, "NOMBRE");
        addTableHeader(table, "PRECIO");
        addTableHeader(table, "COSTO");
        addTableHeader(table, "CATEGORÍAS");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            addTableRow(table, producto.getId().toString());
            addTableRow(table, producto.getNombre());
            addTableRow(table, String.format("$%.2f", producto.getPrecioUnitario()));
            addTableRow(table, String.format("$%.2f", producto.getCosto()));
            addTableRow(table, producto.getCategorias());
        }

        document.add(table);
        addResumenProductosSection(document, reportDTO);
    }

    private void addResumenProductosSection(Document document, ProductoReportDTO reportDTO) throws DocumentException {
        document.add(new Paragraph(" "));

        // Crear tabla para el resumen
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setWidths(new float[]{2, 1});

        // Filas del resumen
        addSummaryRow(summaryTable, "Total de productos:", String.valueOf(reportDTO.getTotalProductos()));
        addSummaryRow(summaryTable, "Valor total inventario:", String.format("$%.2f", reportDTO.getValorTotalInventario()));
        addSummaryRow(summaryTable, "Precio promedio:", String.format("$%.2f", reportDTO.getPrecioPromedio()));

        if (reportDTO.getTotalProductos() > 0) {
            double promedioCategorias = (double) reportDTO.getProductos().stream()
                    .mapToInt(ProductoReportDataDTO::getCantidadCategorias)
                    .sum() / reportDTO.getTotalProductos();
            addSummaryRow(summaryTable, "Promedio categorías/producto:", String.format("%.1f", promedioCategorias));
        }

        document.add(summaryTable);
    }

    public byte[] generateInventarioReport(InventarioReportDTO reportDTO, String tipoReporte) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Horizontal para más columnas
            PdfWriter.getInstance(document, baos);
            document.open();

            // Encabezado
            addInventarioHeader(document, reportDTO);

            // Espacio en blanco
            document.add(new Paragraph(" "));

            // Contenido según el tipo de reporte
            switch (tipoReporte.toLowerCase()) {
                case "bajo-stock":
                    generateBajoStockReport(document, reportDTO);
                    break;
                case "sobre-stock":
                    generateSobreStockReport(document, reportDTO);
                    break;
                case "diario":
                case "semanal":
                case "mensual":
                    generatePeriodicReport(document, reportDTO, tipoReporte);
                    break;
                default:
                    generateCompletoReport(document, reportDTO);
                    break;
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte PDF de inventario", e);
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    private void addInventarioHeader(Document document, InventarioReportDTO reportDTO) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 3});

        // Celda para la imagen
        try {
            Image logo = Image.getInstance(new ClassPathResource("static/reporte.png").getURL());
            logo.scaleToFit(80, 80);
            PdfPCell imageCell = new PdfPCell(logo, true);
            imageCell.setBorder(Rectangle.NO_BORDER);
            imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            imageCell.setPadding(5);
            headerTable.addCell(imageCell);
        } catch (IOException e) {
            log.warn("No se pudo cargar la imagen del logo, usando celda vacía");
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyCell);
        }

        // Celda para la información del reporte
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setPadding(5);

        Paragraph title = new Paragraph(reportDTO.getTitulo(), TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);

        Paragraph date = new Paragraph("Fecha: " + reportDTO.getFechaGeneracion(), NORMAL_FONT);
        date.setAlignment(Element.ALIGN_CENTER);

        if (reportDTO.getPeriodo() != null && !reportDTO.getPeriodo().isEmpty()) {
            Paragraph periodo = new Paragraph(reportDTO.getPeriodo(), NORMAL_FONT);
            periodo.setAlignment(Element.ALIGN_CENTER);
            infoCell.addElement(periodo);
        }

        Paragraph negocioInfo = new Paragraph("Negocio: " + reportDTO.getNegocioNombre(), SMALL_FONT);
        negocioInfo.setAlignment(Element.ALIGN_CENTER);

        infoCell.addElement(title);
        infoCell.addElement(date);
        infoCell.addElement(negocioInfo);
        headerTable.addCell(infoCell);

        document.add(headerTable);
    }

    private void generateBajoStockReport(Document document, InventarioReportDTO reportDTO) throws DocumentException {
        // Tabla para productos con stock bajo
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 3});

        // Encabezados de la tabla
        addTableHeader(table, "ID");
        addTableHeader(table, "PRODUCTO");
        addTableHeader(table, "CATEGORÍA");
        addTableHeader(table, "STOCK ACTUAL");
        addTableHeader(table, "MÍNIMO");
        addTableHeader(table, "DÉFICIT");
        addTableHeader(table, "ESTADO");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            if ("CRÍTICO".equals(producto.getEstadoStock()) || "BAJO".equals(producto.getEstadoStock())) {
                int deficit = producto.getCantidadMinima() - producto.getCantidad();

                addTableRow(table, producto.getId().toString());
                addTableRow(table, producto.getNombre());
                addTableRow(table, producto.getCategoria());
                addTableRow(table, String.valueOf(producto.getCantidad()));
                addTableRow(table, String.valueOf(producto.getCantidadMinima()));
                addTableRow(table, String.valueOf(deficit));

                // Celda con color según el estado
                PdfPCell estadoCell = new PdfPCell(new Phrase(producto.getEstadoStock(), NORMAL_FONT));
                estadoCell.setPadding(5);
                if ("CRÍTICO".equals(producto.getEstadoStock())) {
                    estadoCell.setBackgroundColor(new BaseColor(255, 102, 102)); // Rojo
                } else {
                    estadoCell.setBackgroundColor(new BaseColor(255, 204, 102)); // Amarillo
                }
                table.addCell(estadoCell);
            }
        }

        document.add(table);
        addInventarioResumenSection(document, reportDTO);
    }

    private void generateSobreStockReport(Document document, InventarioReportDTO reportDTO) throws DocumentException {
        // Tabla para productos con sobre stock
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 3});

        // Encabezados de la tabla
        addTableHeader(table, "ID");
        addTableHeader(table, "PRODUCTO");
        addTableHeader(table, "CATEGORÍA");
        addTableHeader(table, "STOCK ACTUAL");
        addTableHeader(table, "MÁXIMO");
        addTableHeader(table, "EXCESO");
        addTableHeader(table, "VALOR EXCESO");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            if ("SOBRE STOCK".equals(producto.getEstadoStock())) {
                int exceso = producto.getCantidad() - producto.getCantidadMaxima();
                double valorExceso = exceso * producto.getPrecioUnitario();

                addTableRow(table, producto.getId().toString());
                addTableRow(table, producto.getNombre());
                addTableRow(table, producto.getCategoria());
                addTableRow(table, String.valueOf(producto.getCantidad()));
                addTableRow(table, String.valueOf(producto.getCantidadMaxima()));
                addTableRow(table, String.valueOf(exceso));
                addTableRow(table, String.format("$%.2f", valorExceso));
            }
        }

        document.add(table);
        addInventarioResumenSection(document, reportDTO);
    }

    private void generatePeriodicReport(Document document, InventarioReportDTO reportDTO, String periodo) throws DocumentException {
        // Tabla para reportes periódicos
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 3, 3});

        // Encabezados de la tabla
        addTableHeader(table, "ID");
        addTableHeader(table, "PRODUCTO");
        addTableHeader(table, "CATEGORÍA");
        addTableHeader(table, "STOCK ACTUAL");
        addTableHeader(table, "MÍNIMO");
        addTableHeader(table, "MÁXIMO");
        addTableHeader(table, "ESTADO");
        addTableHeader(table, "VALOR TOTAL");

        // Datos de los productos
        for (var producto : reportDTO.getProductos()) {
            addTableRow(table, producto.getId().toString());
            addTableRow(table, producto.getNombre());
            addTableRow(table, producto.getCategoria());
            addTableRow(table, String.valueOf(producto.getCantidad()));
            addTableRow(table, String.valueOf(producto.getCantidadMinima()));
            addTableRow(table, String.valueOf(producto.getCantidadMaxima()));

            // Celda con color según el estado
            PdfPCell estadoCell = new PdfPCell(new Phrase(producto.getEstadoStock(), NORMAL_FONT));
            estadoCell.setPadding(5);
            switch (producto.getEstadoStock()) {
                case "CRÍTICO":
                    estadoCell.setBackgroundColor(new BaseColor(255, 102, 102)); // Rojo
                    break;
                case "BAJO":
                    estadoCell.setBackgroundColor(new BaseColor(255, 204, 102)); // Amarillo
                    break;
                case "SOBRE STOCK":
                    estadoCell.setBackgroundColor(new BaseColor(102, 204, 255)); // Azul claro
                    break;
                default:
                    estadoCell.setBackgroundColor(new BaseColor(204, 255, 204)); // Verde claro
                    break;
            }
            table.addCell(estadoCell);

            addTableRow(table, String.format("$%.2f", producto.getValorTotal()));
        }

        document.add(table);
        addInventarioResumenSection(document, reportDTO);
    }

    private void generateCompletoReport(Document document, InventarioReportDTO reportDTO) throws DocumentException {
        generatePeriodicReport(document, reportDTO, "completo");
    }

    private void addInventarioResumenSection(Document document, InventarioReportDTO reportDTO) throws DocumentException {
        document.add(new Paragraph(" "));

        // Crear tabla para el resumen
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(80);
        summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        summaryTable.setWidths(new float[]{2, 1});

        // Filas del resumen
        addSummaryRow(summaryTable, "Total de productos:", String.valueOf(reportDTO.getTotalProductos()));
        addSummaryRow(summaryTable, "Productos con stock normal:", String.valueOf(reportDTO.getProductosStockNormal()));
        addSummaryRow(summaryTable, "Productos con stock bajo:", String.valueOf(reportDTO.getProductosBajoStock()));
        addSummaryRow(summaryTable, "Productos con sobre stock:", String.valueOf(reportDTO.getProductosSobreStock()));
        addSummaryRow(summaryTable, "Valor total del inventario:", String.format("$%.2f", reportDTO.getValorTotalInventario()));

        document.add(summaryTable);
    }
}

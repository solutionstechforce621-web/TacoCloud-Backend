package com.api.Summit.API.reports.pdf.controller;

import com.api.Summit.API.reports.pdf.service.InventarioReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventarioReportController {

    private final InventarioReportService inventarioReportService;

    @GetMapping("/negocio/{negocioId}/pdf")
    public ResponseEntity<byte[]> generateInventarioReportPdf(
            @PathVariable Long negocioId,
            @RequestParam(defaultValue = "completo") String tipo,
            @RequestParam(required = false) String periodo) {

        try {
            byte[] pdfBytes = inventarioReportService.generateInventarioReportPdf(negocioId, tipo, periodo);

            String filename = String.format("reporte_inventario_%s_%d.pdf", tipo, System.currentTimeMillis());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoints específicos para cada tipo de reporte
    @GetMapping("/negocio/{negocioId}/bajo-stock/pdf")
    public ResponseEntity<byte[]> generateBajoStockReport(@PathVariable Long negocioId) {
        return generateInventarioReportPdf(negocioId, "bajo-stock", null);
    }

    @GetMapping("/negocio/{negocioId}/sobre-stock/pdf")
    public ResponseEntity<byte[]> generateSobreStockReport(@PathVariable Long negocioId) {
        return generateInventarioReportPdf(negocioId, "sobre-stock", null);
    }

    @GetMapping("/negocio/{negocioId}/diario/pdf")
    public ResponseEntity<byte[]> generateDiarioReport(@PathVariable Long negocioId) {
        return generateInventarioReportPdf(negocioId, "diario", null);
    }

    @GetMapping("/negocio/{negocioId}/semanal/pdf")
    public ResponseEntity<byte[]> generateSemanalReport(@PathVariable Long negocioId) {
        return generateInventarioReportPdf(negocioId, "semanal", null);
    }

    @GetMapping("/negocio/{negocioId}/mensual/pdf")
    public ResponseEntity<byte[]> generateMensualReport(@PathVariable Long negocioId) {
        return generateInventarioReportPdf(negocioId, "mensual", null);
    }
}

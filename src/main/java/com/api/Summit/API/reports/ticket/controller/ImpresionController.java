package com.api.Summit.API.reports.ticket.controller;

import com.api.Summit.API.reports.ticket.service.ThermalPrintService;
import com.api.Summit.API.service.interfaces.PedidoVentaService;
import com.api.Summit.API.view.dto.PedidoVentaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/impresion")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ImpresionController {

    private final PedidoVentaService pedidoVentaService;
    private final ThermalPrintService thermalPrintService;

    /**
     * Imprimir ticket de cocina
     */
    @PostMapping("/pedidos/{pedidoId}/negocio/{negocioId}/ticket-cocina")
    public ResponseEntity<String> imprimirTicketCocina(
            @PathVariable Long pedidoId,
            @PathVariable Long negocioId,
            @RequestParam String printerName) {

        try {
            PedidoVentaDTO pedido = pedidoVentaService.findByIdAndNegocioId(pedidoId, negocioId);
            thermalPrintService.printKitchenTicket(pedido, printerName);

            return ResponseEntity.ok("Ticket de cocina impreso correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al imprimir: " + e.getMessage());
        }
    }

    /**
     * Imprimir ticket de cliente
     */
    @PostMapping("/pedidos/{pedidoId}/negocio/{negocioId}/ticket-cliente")
    public ResponseEntity<String> imprimirTicketCliente(
            @PathVariable Long pedidoId,
            @PathVariable Long negocioId,
            @RequestParam String printerName) {

        try {
            PedidoVentaDTO pedido = pedidoVentaService.findByIdAndNegocioId(pedidoId, negocioId);
            thermalPrintService.printCustomerTicket(pedido, printerName);

            return ResponseEntity.ok("Ticket de cliente impreso correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al imprimir: " + e.getMessage());
        }
    }

    /**
     * Imprimir ambos tickets
     */
    @PostMapping("/pedidos/{pedidoId}/negocio/{negocioId}/tickets")
    public ResponseEntity<String> imprimirAmbosTickets(
            @PathVariable Long pedidoId,
            @PathVariable Long negocioId,
            @RequestParam String printerName) {

        try {
            PedidoVentaDTO pedido = pedidoVentaService.findByIdAndNegocioId(pedidoId, negocioId);

            // Imprimir ticket de cocina primero
            thermalPrintService.printKitchenTicket(pedido, printerName);

            // Pequeña pausa entre impresiones
            Thread.sleep(1000);

            // Imprimir ticket de cliente
            thermalPrintService.printCustomerTicket(pedido, printerName);

            return ResponseEntity.ok("Ambos tickets impresos correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al imprimir: " + e.getMessage());
        }
    }
}

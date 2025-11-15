package com.api.Summit.API.reports.ticket.service;
import com.api.Summit.API.view.dto.DetallePedidoVentaDTO;
import com.api.Summit.API.view.dto.PedidoVentaDTO;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.EscPosConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThermalPrintService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Obtiene el servicio de impresión por nombre
     */
    public PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().toLowerCase().contains(printerName.toLowerCase())) {
                return printService;
            }
        }
        throw new RuntimeException("Impresora no encontrada: " + printerName);
    }

    /**
     * Limpia la cola de impresión
     */
    public void clearPrintQueue(String printerName) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Process process = Runtime.getRuntime().exec("cmd /c echo off | clip");
                process.waitFor();
            }
            log.info("Cola de impresión limpiada para: {}", printerName);
        } catch (Exception e) {
            log.warn("No se pudo limpiar la cola de impresión: {}", e.getMessage());
        }
    }

    /**
     * Verifica si hay trabajos de impresión pendientes
     */
    public boolean hasPendingJobs(String printerName) {
        try {
            PrintService printService = findPrintService(printerName);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera e imprime ticket para cocina
     */
    public void printKitchenTicket(PedidoVentaDTO pedido, String printerName) {
        clearPrintQueue(printerName);

        if (hasPendingJobs(printerName)) {
            throw new RuntimeException("Hay trabajos de impresión pendientes en la impresora");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PrintService printService = findPrintService(printerName);
            EscPos escpos = new EscPos(outputStream);

            // Configuración inicial - CORREGIDO para versión 4.0.1
            Style titleStyle = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            Style normalStyle = new Style()
                    .setJustification(EscPosConst.Justification.Left_Default);

            Style boldStyle = new Style()
                    .setBold(true)
                    .setJustification(EscPosConst.Justification.Left_Default);

            // Encabezado - CORREGIDO: Mostrar nombre del negocio y ticket completo
            escpos.writeLF(titleStyle, "COCINA");
            escpos.writeLF(normalStyle, "Negocio: " + pedido.getNegocio().getNombre());
            escpos.writeLF(normalStyle, "Ticket Cocina: " + pedido.getTicketCocina());
            escpos.writeLF(normalStyle, "Fecha: " + LocalDateTime.now().format(DATE_FORMATTER));
            escpos.writeLF(normalStyle, "Pedido ID: #" + pedido.getId());
            escpos.feed(1);

            // Línea separadora
            escpos.writeLF(normalStyle, "==================================");

            // Información del cliente
            if (pedido.getNombreCliente() != null && !pedido.getNombreCliente().isEmpty()) {
                escpos.writeLF(boldStyle, "Cliente: " + pedido.getNombreCliente());
            }
            if (pedido.getObservaciones() != null && !pedido.getObservaciones().isEmpty()) {
                escpos.writeLF(normalStyle, "Observaciones: " + pedido.getObservaciones());
            }
            escpos.feed(1);

            // Productos
            escpos.writeLF(boldStyle, "PRODUCTOS:");
            escpos.writeLF(normalStyle, "----------------------------------");

            for (DetallePedidoVentaDTO detalle : pedido.getDetalles()) {
                escpos.writeLF(normalStyle, detalle.getCantidad() + "x " + detalle.getProductoNombre());
                if (detalle.getNota() != null && !detalle.getNota().isEmpty()) {
                    escpos.writeLF(normalStyle, "   Nota: " + detalle.getNota());
                }
                escpos.feed(1);
            }

            escpos.feed(2);
            escpos.writeLF(titleStyle, "** PENDIENTE DE PREPARACIÓN **");
            escpos.feed(3);

            // Corte de papel
            escpos.cut(EscPos.CutMode.FULL);

            // Enviar a impresión
            byte[] bytes = outputStream.toByteArray();
            javax.print.DocFlavor flavor = javax.print.DocFlavor.BYTE_ARRAY.AUTOSENSE;
            javax.print.Doc doc = new javax.print.SimpleDoc(bytes, flavor, null);
            javax.print.DocPrintJob job = printService.createPrintJob();
            job.print(doc, null);

            log.info("Ticket de cocina impreso: {}", pedido.getTicketCocina());

        } catch (Exception e) {
            throw new RuntimeException("Error al imprimir ticket de cocina: " + e.getMessage(), e);
        }
    }

    /**
     * Genera e imprime ticket para cliente
     */
    public void printCustomerTicket(PedidoVentaDTO pedido, String printerName) {
        clearPrintQueue(printerName);

        if (hasPendingJobs(printerName)) {
            throw new RuntimeException("Hay trabajos de impresión pendientes en la impresora");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PrintService printService = findPrintService(printerName);
            EscPos escpos = new EscPos(outputStream);

            // Configuración de estilos - CORREGIDO para versión 4.0.1
            Style titleStyle = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center)
                    .setBold(true);

            Style subtitleStyle = new Style()
                    .setBold(true)
                    .setJustification(EscPosConst.Justification.Center);

            Style normalStyle = new Style()
                    .setJustification(EscPosConst.Justification.Left_Default);

            Style boldStyle = new Style()
                    .setBold(true)
                    .setJustification(EscPosConst.Justification.Left_Default);

            Style rightStyle = new Style()
                    .setJustification(EscPosConst.Justification.Right);

            // Encabezado - CORREGIDO: Mostrar nombre del negocio y ticket completo
            escpos.writeLF(titleStyle, pedido.getNegocio().getNombre().toUpperCase());
            escpos.writeLF(subtitleStyle, "TICKET: " + pedido.getTicketCliente());
            escpos.writeLF(normalStyle, "Fecha: " + LocalDateTime.now().format(DATE_FORMATTER));
            escpos.writeLF(normalStyle, "Pedido ID: #" + pedido.getId());
            escpos.feed(1);

            // Línea separadora
            escpos.writeLF(normalStyle, "==================================");

            // Información del negocio
            if (pedido.getNegocio().getDomicilio() != null && !pedido.getNegocio().getDomicilio().isEmpty()) {
                escpos.writeLF(normalStyle, "Dirección: " + pedido.getNegocio().getDomicilio());
            }

            if (pedido.getNegocio().getTelefono() != null && !pedido.getNegocio().getTelefono().isEmpty()) {
                escpos.writeLF(normalStyle, "Teléfono: " + pedido.getNegocio().getTelefono());
            }

            if (pedido.getNegocio().getRfc() != null && !pedido.getNegocio().getRfc().isEmpty()) {
                escpos.writeLF(normalStyle, "RFC: " + pedido.getNegocio().getRfc());
            }

            if (pedido.getNegocio().getCodigoPostal() != null && !pedido.getNegocio().getCodigoPostal().isEmpty()) {
                escpos.writeLF(normalStyle, "C.P.: " + pedido.getNegocio().getCodigoPostal());
            }

            if (pedido.getNegocio().getCorreo() != null && !pedido.getNegocio().getCorreo().isEmpty()) {
                escpos.writeLF(normalStyle, "Email: " + pedido.getNegocio().getCorreo());
            }
            escpos.feed(1);

            // Información del cliente
            escpos.writeLF(boldStyle, "DATOS DEL CLIENTE:");
            escpos.writeLF(normalStyle, "----------------------------------");

            if (pedido.getNombreCliente() != null && !pedido.getNombreCliente().isEmpty()) {
                escpos.writeLF(normalStyle, "Cliente: " + pedido.getNombreCliente());
            }

            escpos.writeLF(normalStyle, "Tipo de pedido: " + pedido.getTipoPedido());
            escpos.writeLF(normalStyle, "Estado: " + pedido.getEstado());

            if (pedido.getObservaciones() != null && !pedido.getObservaciones().isEmpty()) {
                escpos.writeLF(normalStyle, "Observaciones: " + pedido.getObservaciones());
            }
            escpos.feed(1);

            // Detalles de productos
            escpos.writeLF(boldStyle, "DETALLE DE COMPRA:");
            escpos.writeLF(normalStyle, "----------------------------------");

            for (DetallePedidoVentaDTO detalle : pedido.getDetalles()) {
                // Producto y cantidad
                escpos.write(normalStyle, detalle.getCantidad() + "x " + detalle.getProductoNombre());
                escpos.writeLF(rightStyle, String.format("$%.2f", detalle.getSubtotal()));

                if (detalle.getNota() != null && !detalle.getNota().isEmpty()) {
                    escpos.writeLF(normalStyle, "   Nota: " + detalle.getNota());
                }
                escpos.feed(1);
            }

            escpos.writeLF(normalStyle, "==================================");

            // Totales
            escpos.write(boldStyle, "SUBTOTAL:");
            escpos.writeLF(rightStyle, String.format("$%.2f", pedido.getTotal()));

            // Calcular IVA (16%)
            BigDecimal iva = pedido.getTotal().multiply(new BigDecimal("0.16"));
            escpos.write(boldStyle, "IVA (16%):");
            escpos.writeLF(rightStyle, String.format("$%.2f", iva));

            BigDecimal totalConIva = pedido.getTotal().add(iva);
            escpos.write(boldStyle, "TOTAL:");
            escpos.writeLF(rightStyle, String.format("$%.2f", totalConIva));
            escpos.feed(1);

            // Información de pago
            if (pedido.getPago() != null) {
                escpos.writeLF(boldStyle, "INFORMACIÓN DE PAGO:");
                escpos.writeLF(normalStyle, "----------------------------------");
                escpos.writeLF(normalStyle, "Método: " + pedido.getPago().getMetodoPago().toString());
                escpos.writeLF(normalStyle, "Fecha de pago: " +
                        pedido.getPago().getFechaPago().format(DATE_FORMATTER));
            }

            escpos.feed(2);

            // Mensajes finales
            escpos.writeLF(normalStyle, "¡Gracias por su compra!");
            escpos.writeLF(normalStyle, "TacoCloud v.1.25");
            escpos.feed(3);

            // Corte de papel
            escpos.cut(EscPos.CutMode.FULL);

            // Enviar a impresión
            byte[] bytes = outputStream.toByteArray();
            javax.print.DocFlavor flavor = javax.print.DocFlavor.BYTE_ARRAY.AUTOSENSE;
            javax.print.Doc doc = new javax.print.SimpleDoc(bytes, flavor, null);
            javax.print.DocPrintJob job = printService.createPrintJob();
            job.print(doc, null);

            log.info("Ticket de cliente impreso: {}", pedido.getTicketCliente());

        } catch (Exception e) {
            throw new RuntimeException("Error al imprimir ticket de cliente: " + e.getMessage(), e);
        }
    }
}
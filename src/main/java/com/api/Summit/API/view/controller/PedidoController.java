package com.api.Summit.API.view.controller;

import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.service.exception.ApiResponse;
import com.api.Summit.API.service.interfaces.PedidoVentaService;
import com.api.Summit.API.view.dto.PedidoRequestDTO;
import com.api.Summit.API.view.dto.PedidoVentaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoVentaService pedidoVentaService;

    // ➕ Crear pedido
    @PostMapping("/negocio/{negocioId}")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> createPedido(
            @RequestBody PedidoRequestDTO pedidoRequestDTO,
            @PathVariable Long negocioId) {

        try {
            PedidoVentaDTO nuevoPedido = pedidoVentaService.createPedido(pedidoRequestDTO, negocioId);
            return ResponseEntity.ok(ApiResponse.success(nuevoPedido, "Pedido creado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 📋 Listar todos los pedidos por negocio
    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<ApiResponse<Page<PedidoVentaDTO>>> getPedidosByNegocio(
            @PathVariable Long negocioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoVentaDTO> pedidos = pedidoVentaService.findAllByNegocioId(negocioId, pageable);
        return ResponseEntity.ok(ApiResponse.success(pedidos, "Pedidos obtenidos correctamente"));
    }

    // 📋 Listar pedidos por estado
    @GetMapping("/negocio/{negocioId}/estado/{estado}")
    public ResponseEntity<ApiResponse<Page<PedidoVentaDTO>>> getPedidosByEstado(
            @PathVariable Long negocioId,
            @PathVariable EstadoPedido estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoVentaDTO> pedidos = pedidoVentaService.findByEstadoAndNegocioId(estado, negocioId, pageable);
        return ResponseEntity.ok(ApiResponse.success(pedidos, "Pedidos por estado obtenidos correctamente"));
    }

    // 📋 Listar pedidos por múltiples estados
    @GetMapping("/negocio/{negocioId}/estados")
    public ResponseEntity<ApiResponse<Page<PedidoVentaDTO>>> getPedidosByEstados(
            @PathVariable Long negocioId,
            @RequestParam List<EstadoPedido> estados,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PedidoVentaDTO> pedidos = pedidoVentaService.findByEstadosAndNegocioId(estados, negocioId, pageable);
        return ResponseEntity.ok(ApiResponse.success(pedidos, "Pedidos por estados obtenidos correctamente"));
    }

    // 🔍 Obtener pedido por ID y negocio
    @GetMapping("/{id}/negocio/{negocioId}")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> getPedidoById(
            @PathVariable Long id,
            @PathVariable Long negocioId) {

        try {
            PedidoVentaDTO pedido = pedidoVentaService.findByIdAndNegocioId(id, negocioId);
            return ResponseEntity.ok(ApiResponse.success(pedido, "Pedido obtenido correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ✏️ Actualizar pedido
    @PutMapping("/{id}/negocio/{negocioId}")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> updatePedido(
            @PathVariable Long id,
            @RequestBody PedidoRequestDTO pedidoRequestDTO,
            @PathVariable Long negocioId) {

        try {
            PedidoVentaDTO pedidoActualizado = pedidoVentaService.updatePedido(id, pedidoRequestDTO, negocioId);
            return ResponseEntity.ok(ApiResponse.success(pedidoActualizado, "Pedido actualizado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 💵 Marcar como pagado
    @PostMapping("/{id}/negocio/{negocioId}/pagar")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> marcarComoPagado(
            @PathVariable Long id,
            @PathVariable Long negocioId,
            @RequestParam String metodoPago) {

        try {
            PedidoVentaDTO pedidoPagado = pedidoVentaService.marcarComoPagado(id, negocioId, metodoPago);
            return ResponseEntity.ok(ApiResponse.success(pedidoPagado, "Pedido marcado como pagado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🔄 Cambiar estado del pedido
    @PatchMapping("/{id}/negocio/{negocioId}/estado")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> cambiarEstado(
            @PathVariable Long id,
            @PathVariable Long negocioId,
            @RequestParam EstadoPedido nuevoEstado) {

        try {
            PedidoVentaDTO pedidoActualizado = pedidoVentaService.cambiarEstado(id, negocioId, nuevoEstado);
            return ResponseEntity.ok(ApiResponse.success(pedidoActualizado, "Estado del pedido actualizado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ❌ Cancelar pedido
    @PostMapping("/{id}/negocio/{negocioId}/cancelar")
    public ResponseEntity<ApiResponse<PedidoVentaDTO>> cancelarPedido(
            @PathVariable Long id,
            @PathVariable Long negocioId) {

        try {
            PedidoVentaDTO pedidoCancelado = pedidoVentaService.cancelarPedido(id, negocioId);
            return ResponseEntity.ok(ApiResponse.success(pedidoCancelado, "Pedido cancelado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🗑️ Eliminar pedido
    @DeleteMapping("/{id}/negocio/{negocioId}")
    public ResponseEntity<ApiResponse<String>> deletePedido(
            @PathVariable Long id,
            @PathVariable Long negocioId) {

        try {
            pedidoVentaService.deletePedido(id, negocioId);
            return ResponseEntity.ok(ApiResponse.success("Pedido eliminado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 🍳 Obtener pedidos para cocina
    @GetMapping("/negocio/{negocioId}/cocina")
    public ResponseEntity<ApiResponse<List<PedidoVentaDTO>>> getPedidosCocina(
            @PathVariable Long negocioId) {

        List<PedidoVentaDTO> pedidosCocina = pedidoVentaService.findPedidosCocinaByNegocioId(negocioId);
        return ResponseEntity.ok(ApiResponse.success(pedidosCocina, "Pedidos de cocina obtenidos correctamente"));
    }

    // 📊 Obtener estadísticas de pedidos
    @GetMapping("/negocio/{negocioId}/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getEstadisticasPedidos(
            @PathVariable Long negocioId) {

        long totalPedidos = pedidoVentaService.countByNegocioId(negocioId);
        long pendientes = pedidoVentaService.countByEstadoAndNegocioId(EstadoPedido.PENDIENTE, negocioId);
        long enPreparacion = pedidoVentaService.countByEstadoAndNegocioId(EstadoPedido.EN_PREPARACION, negocioId);
        long listos = pedidoVentaService.countByEstadoAndNegocioId(EstadoPedido.LISTO, negocioId);
        long pagados = pedidoVentaService.countByEstadoAndNegocioId(EstadoPedido.PAGADO, negocioId);

        Map<String, Long> estadisticas = Map.of(
                "totalPedidos", totalPedidos,
                "pendientes", pendientes,
                "enPreparacion", enPreparacion,
                "listos", listos,
                "pagados", pagados
        );

        return ResponseEntity.ok(ApiResponse.success(estadisticas, "Estadísticas obtenidas correctamente"));
    }

    // 📊 Obtener total de pedidos por negocio
    @GetMapping("/negocio/{negocioId}/total")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTotalPedidosByNegocio(
            @PathVariable Long negocioId) {

        long totalPedidos = pedidoVentaService.countByNegocioId(negocioId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("totalPedidos", totalPedidos), "Total de pedidos obtenido"));
    }
}
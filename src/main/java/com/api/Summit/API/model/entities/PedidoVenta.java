package com.api.Summit.API.model.entities;

import com.api.Summit.API.model.enums.EstadoPedido;
import com.api.Summit.API.model.enums.TipoPedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pedido", nullable = false)
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @Column(name = "ticket_cocina", length = 50)
    private String ticketCocina;

    @Column(name = "ticket_cliente", length = 50)
    private String ticketCliente;

    @Column(name = "nombre_cliente", length = 100)
    private String nombreCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedidoVenta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // Esto asegura que la lista se inicialice
    private List<DetallePedidoVenta> detalles = new ArrayList<>();

    @OneToOne(mappedBy = "pedidoVenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pago;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id")
    private Caja caja;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper para relación bidireccional
    public void addDetalle(DetallePedidoVenta detalle) {
        this.detalles.add(detalle);
        detalle.setPedidoVenta(this);
    }
}
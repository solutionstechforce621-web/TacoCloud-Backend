package com.api.Summit.API.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cajas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "saldo_inicial", nullable = false)
    private double saldoInicial;

    @Column(name = "saldo_actual", nullable = false)
    private double saldoActual;

    @Column(name = "estado", nullable = false)
    private boolean estado; // true = abierta, false = cerrada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PedidoVenta> pedidos = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

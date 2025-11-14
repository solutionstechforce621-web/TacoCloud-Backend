package com.api.Summit.API.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "negocios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negocio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "domicilio", length = 120)
    private String domicilio;

    @Column(name = "rfc", length = 30)
    private String rfc;

    @Column(name = "codigoPostal", length = 10)
    private String codigoPostal;

    @Column(name = "correo", length = 80)
    private String correo;

    @Column(name = "telefono", length= 20)
    private String telefono;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con usuarios (dueños/administradores del negocio)
    @ManyToMany(mappedBy = "negocios", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> usuarios = new HashSet<>();

    // Nuevas relaciones agregadas
    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PedidoVenta> pedidoVentas = new HashSet<>(); // ✅ NUEVA

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Caja> cajas = new HashSet<>();

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Inventario> inventarios = new HashSet<>();

    // Métodos helper para la relación bidireccional
    public void addUsuario(User usuario) {
        this.usuarios.add(usuario);
        usuario.getNegocios().add(this);
    }

    public void removeUsuario(User usuario) {
        this.usuarios.remove(usuario);
        usuario.getNegocios().remove(this);
    }
}

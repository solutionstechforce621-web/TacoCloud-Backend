package com.api.Summit.API.service.impl;

import com.api.Summit.API.model.entities.Categoria;
import com.api.Summit.API.model.entities.Negocio;
import com.api.Summit.API.model.entities.Producto;
import com.api.Summit.API.model.repository.CategoriaRepository;
import com.api.Summit.API.model.repository.NegocioRepository;
import com.api.Summit.API.model.repository.ProductoRepository;
import com.api.Summit.API.reports.excel.service.ProductoExcelReportService;
import com.api.Summit.API.reports.pdf.service.ProductoReportService;
import com.api.Summit.API.service.exception.BusinessException;
import com.api.Summit.API.service.exception.ResourceNotFoundException;
import com.api.Summit.API.service.interfaces.ProductoService;
import com.api.Summit.API.view.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final NegocioRepository negocioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoReportService productoReportService;
    private final ProductoExcelReportService productoExcelReportService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Use findAllByNegocioId instead");
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO findById(Long id) {
        throw new UnsupportedOperationException("Use findByIdAndNegocioId instead");
    }

    @Override
    @Transactional
    public ProductoDTO save(ProductoRequestDTO productoRequestDTO) {
        throw new UnsupportedOperationException("Use saveWithNegocio instead");
    }

    @Override
    @Transactional
    public ProductoDTO update(Long id, ProductoRequestDTO productoRequestDTO) {
        throw new UnsupportedOperationException("Use updateWithNegocio instead");
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Use deleteByIdAndNegocioId instead");
    }

    // Listar productos por negocio
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findAllByNegocioId(Long negocioId, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByNegocioId(negocioId, pageable);
        return productos.map(this::convertToDTO);
    }

    // Buscar producto por ID y negocio
    @Transactional(readOnly = true)
    public ProductoDTO findByIdAndNegocioId(Long id, Long negocioId) {
        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id + " para el negocio: " + negocioId));
        return convertToDTO(producto);
    }

    // Crear producto asociado a un negocio
    // Crear producto asociado a un negocio
    @Transactional
    public ProductoDTO saveWithNegocio(ProductoRequestDTO productoRequestDTO, Long negocioId) {
        // Verificar si ya existe un producto con el mismo nombre en este negocio
        if (productoRepository.existsByNombreAndNegocioId(productoRequestDTO.getNombre(), negocioId)) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + productoRequestDTO.getNombre() + " en este negocio");
        }

        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        Producto producto = new Producto();
        producto.setNombre(productoRequestDTO.getNombre());
        producto.setPrecioUnitario(productoRequestDTO.getPrecioUnitario());
        producto.setCosto(productoRequestDTO.getCosto());
        producto.setNegocio(negocio);

        // Asignar categorías si se proporcionan - CORREGIDO
        if (productoRequestDTO.getCategoriasIds() != null && !productoRequestDTO.getCategoriasIds().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>();

            for (Long categoriaId : productoRequestDTO.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + categoriaId));

                // Verificar que la categoría pertenezca al mismo negocio
                if (!categoria.getNegocio().getId().equals(negocioId)) {
                    throw new RuntimeException("La categoría con ID: " + categoriaId + " no pertenece al negocio: " + negocioId);
                }

                categorias.add(categoria);
            }

            producto.setCategorias(categorias);

            // Establecer la relación bidireccional
            for (Categoria categoria : categorias) {
                categoria.getProductos().add(producto);
            }
        }

        Producto productoGuardado = productoRepository.save(producto);
        return convertToDTO(productoGuardado);
    }

    // Actualizar producto verificando el negocio
    // Actualizar producto verificando el negocio
    @Transactional
    public ProductoDTO updateWithNegocio(Long id, ProductoRequestDTO productoRequestDTO, Long negocioId) {
        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id + " para el negocio: " + negocioId));

        // Verificar si el nuevo nombre ya existe en otro producto del mismo negocio
        if (productoRepository.existsByNombreAndNegocioIdAndIdNot(productoRequestDTO.getNombre(), negocioId, id)) {
            throw new RuntimeException("Ya existe otro producto con el nombre: " + productoRequestDTO.getNombre() + " en este negocio");
        }

        producto.setNombre(productoRequestDTO.getNombre());
        producto.setPrecioUnitario(productoRequestDTO.getPrecioUnitario());
        producto.setCosto(productoRequestDTO.getCosto());

        // Actualizar categorías si se proporcionan - CORREGIDO
        if (productoRequestDTO.getCategoriasIds() != null) {
            // Limpiar categorías existentes
            for (Categoria categoria : new HashSet<>(producto.getCategorias())) {
                categoria.getProductos().remove(producto);
            }
            producto.getCategorias().clear();

            // Agregar nuevas categorías
            Set<Categoria> nuevasCategorias = new HashSet<>();
            for (Long categoriaId : productoRequestDTO.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + categoriaId));

                // Verificar que la categoría pertenezca al mismo negocio
                if (!categoria.getNegocio().getId().equals(negocioId)) {
                    throw new RuntimeException("La categoría con ID: " + categoriaId + " no pertenece al negocio: " + negocioId);
                }

                nuevasCategorias.add(categoria);
                categoria.getProductos().add(producto);
            }

            producto.setCategorias(nuevasCategorias);
        }

        Producto productoActualizado = productoRepository.save(producto);
        return convertToDTO(productoActualizado);
    }

    // Buscar productos por nombre en un negocio específico
    @Transactional(readOnly = true)
    public Page<ProductoDTO> searchByNombreAndNegocioId(String nombre, Long negocioId, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByNegocioIdAndNombreContainingIgnoreCase(negocioId, nombre, pageable);
        return productos.map(this::convertToDTO);
    }

    // Eliminar producto verificando el negocio
    @Transactional
    public void deleteByIdAndNegocioId(Long id, Long negocioId) {
        Producto producto = productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id + " para el negocio: " + negocioId));

        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateProductosReportPdf(Long negocioId, String tipoReporte) {
        return productoReportService.generateProductosReportPdf(negocioId, tipoReporte);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateProductosExcelReport(Long negocioId, String tipoReporte) {
        return productoExcelReportService.generateProductosExcelReport(negocioId, tipoReporte);
    }

    // Método de conversión a DTO
    // Método de conversión a DTO
    private ProductoDTO convertToDTO(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precioUnitario(producto.getPrecioUnitario())
                .costo(producto.getCosto())
                .negocioId(producto.getNegocio().getId())
                .categorias(producto.getCategorias() != null ?
                        producto.getCategorias().stream()
                                .map(categoria -> CategoriaSimpleDTO.builder() // Usar CategoriaSimpleDTO
                                        .id(categoria.getId())
                                        .nombre(categoria.getNombre())
                                        .descripcion(categoria.getDescripcion())
                                        .negocioId(categoria.getNegocio().getId()) // Agregar negocioId
                                        .build())
                                .collect(Collectors.toSet()) : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findEntityByIdAndNegocioId(Long id, Long negocioId) {
        return productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id + " para el negocio: " + negocioId));
    }
}

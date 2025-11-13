package com.api.Summit.API.service.interfaces;
import com.api.Summit.API.model.entities.Producto;
import com.api.Summit.API.view.dto.ProductoDTO;
import com.api.Summit.API.view.dto.ProductoDetailDTO;
import com.api.Summit.API.view.dto.ProductoRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoDTO> findAll(Pageable pageable);
    ProductoDTO findById(Long id);
    ProductoDTO save(ProductoRequestDTO productoRequestDTO);
    ProductoDTO update(Long id, ProductoRequestDTO productoRequestDTO);
    void deleteById(Long id);
    Producto findEntityByIdAndNegocioId(Long id, Long negocioId);
    // Métodos con filtro por negocio
    Page<ProductoDTO> findAllByNegocioId(Long negocioId, Pageable pageable);
    ProductoDTO findByIdAndNegocioId(Long id, Long negocioId);
    ProductoDTO saveWithNegocio(ProductoRequestDTO productoRequestDTO, Long negocioId);
    ProductoDTO updateWithNegocio(Long id, ProductoRequestDTO productoRequestDTO, Long negocioId);
    Page<ProductoDTO> searchByNombreAndNegocioId(String nombre, Long negocioId, Pageable pageable);
    void deleteByIdAndNegocioId(Long id, Long negocioId);
    byte[] generateProductosReportPdf(Long negocioId, String tipoReporte);
    byte[] generateProductosExcelReport(Long negocioId, String tipoReporte);
}

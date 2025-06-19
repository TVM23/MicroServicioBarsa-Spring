package com.access.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoPaginationDTO;
import com.access.model.Producto;
import com.access.repository.ProductoRepository;

@Service
public class ProductoService {
	  private final ProductoRepository productoRepository;
	  
	  public ProductoService(ProductoRepository productoRepository) {
		  this.productoRepository = productoRepository;
	  }
	  	  
	  public List<Producto> getProductoByCodigo(String codigo) {
		  List<Producto> prod = productoRepository.getProductoByCodigo(codigo);
	      return prod;
	  }
	  
	  public List<Producto> getProductoDecripcionByCodigo(String codigo) {     
	      List<Producto> prod = productoRepository.getProductoDecripcionByCodigo(codigo);
	      return prod;
	  }
	  
	  public PaginationResult<List<Producto>> getProductosFiltrados(ProductoPaginationDTO dto) {
		    int pageValue = dto.getPage();
		    int limitValue = dto.getLimit();
		    int offset = (pageValue - 1) * limitValue;

		    // Consulta base
		    StringBuilder sql = new StringBuilder();
		    List<Object> params = new ArrayList<>(); // Lista para almacenar los parámetros

		    // Filtros dinámicos
		    if (dto.getCodigo() != null) {
		        sql.append(" AND Codigo LIKE ?");
		        params.add("%" + dto.getCodigo() + "%");
		    }
		    if (dto.getDescripcion() != null) {
		        sql.append(" AND Descripcion LIKE ?");
		        params.add("%" + dto.getDescripcion() + "%");
		    }
		    if (dto.getUnidad() != null) {
		        sql.append(" AND Unidad = ?");
		        params.add(dto.getUnidad());
		    }
		    if (dto.getCosto() != null) {
		        sql.append(" AND Costo = ?");
		        params.add(dto.getCosto());
		    }
		    if (dto.getVenta() != null) {
		        sql.append(" AND Venta = ?");
		        params.add(dto.getVenta());
		    }
		    if (dto.getEan() != null) {
		        sql.append(" AND EAN LIKE ?");
		        params.add("%" + dto.getEan() + "%");
		    }
		    if (dto.getSku() != null) {
		        sql.append(" AND SKU LIKE ?");
		        params.add("%" + dto.getSku() + "%");
		    }
		    if (dto.getTapices() != null) {
		        sql.append(" AND Tapices = ?");
		        params.add(dto.getTapices());
		    }
		    if (dto.getBorrado() != null) {
		        sql.append(" AND Borrado = ?");
		        params.add(dto.getBorrado());
		    }

		    // Contar el total de registros
		    int totalItems = productoRepository.contarElementosProductos(sql.toString(), params);

		    // Calcular el número total de páginas
		    int totalPages = (int) Math.ceil((double) totalItems / limitValue);

		    // Consulta paginada
	        List<Producto> data = productoRepository.getProductosList(sql.toString(), params, limitValue, offset);

		    // Retornar el resultado paginado
		    return new PaginationResult<>(totalItems, totalPages, pageValue, data);
		}

}

package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoPaginationDTO;
import com.access.model.Colores;
import com.access.model.Producto;

@Service
public class ProductoService {
	  private final JdbcTemplate jdbcTemplate;
	  
	  public ProductoService(JdbcTemplate jdbcTemplate) {
		  this.jdbcTemplate = jdbcTemplate;
	  }
	  
	  private Producto convert(ResultSet rs) throws SQLException {
	    	 Producto producto = new Producto();
	            producto.setCodigo(rs.getString("Codigo"));
	            producto.setDescripcion(rs.getString("Descripcion"));
	            producto.setUnidad(rs.getString("Unidad"));
	            producto.setCosto(rs.getDouble("Costo"));
	            producto.setVenta(rs.getDouble("Venta"));
	            producto.setExistencia(rs.getInt("Existencia"));
	            producto.setInventarioInicial(rs.getInt("InventarioInicial"));
	            producto.setSueldo(rs.getDouble("Sueldo"));
	            producto.setPrestaciones(rs.getDouble("Prestaciones"));
	            producto.setAportaciones(rs.getDouble("Aportaciones"));
	            producto.setDocumento(rs.getString("Documento"));
	            producto.setEAN(rs.getString("EAN"));
	            producto.setSKU(rs.getString("SKU"));
	            producto.setTapices(rs.getBoolean("Tapices"));
	            producto.setBorrado(rs.getBoolean("Borrado"));
	            return producto;
	    }
	  
	  public List<Producto> getProductoCodigo(String codigo) {
		  String sql = "Select * FROM Producto WHERE Codigo = ?";
		  return jdbcTemplate.query(sql, (rs, rowNum) -> convert(rs), codigo);
	  }
	  
	  public PaginationResult<List<Producto>> getProductosFiltrados(ProductoPaginationDTO dto) {
		    int pageValue = dto.getPage();
		    int limitValue = dto.getLimit();
		    int offset = (pageValue - 1) * limitValue;

		    // Consulta base
		    StringBuilder sql = new StringBuilder("FROM Producto WHERE 1=1");
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
		    String countSql = "SELECT COUNT(*) AS total " + sql.toString();
		    int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());

		    // Calcular el número total de páginas
		    int totalPages = (int) Math.ceil((double) totalItems / limitValue);

		    // Consulta paginada
		    String paginatedSql = "SELECT * " + sql.toString() + " LIMIT ? OFFSET ?";
		    params.add(limitValue); // Agregar LIMIT como parámetro
		    params.add(offset);     // Agregar OFFSET como parámetro

		    // Ejecutar la consulta paginada
		    List<Producto> data = jdbcTemplate.query(paginatedSql, (rs, rowNum) -> {
		        return convert(rs);
		    }, params.toArray());

		    // Retornar el resultado paginado
		    return new PaginationResult<>(totalItems, totalPages, pageValue, data);
		}
	  
	  
	  public List<Producto> getProductoDecripcionByCodigo(String codigo) {
		  String sql = "Select Descripcion FROM Producto WHERE Codigo = ?";
	       return jdbcTemplate.query(sql, (rs, rowNum) -> {
	           return convert(rs);
	       }, codigo);
	  }
	

}

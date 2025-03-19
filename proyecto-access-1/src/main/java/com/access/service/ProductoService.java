package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoPaginationDTO;
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
	  
	  public PaginationResult<List<Producto>> getProductosFiltrados(ProductoPaginationDTO dto) {
	        int pageValue = dto.getPage();
	        int limitValue = dto.getLimit();
	        int offset = (pageValue - 1) * limitValue;
	    	
	        String sql = "FROM Producto WHERE 1=1";
	        
	        if (dto.getCodigo() != null) {
	            sql += " AND Codigo LIKE '%" + dto.getCodigo() + "%'";
	        }
	        if (dto.getDescripcion() != null) {
	            sql += " AND Descripcion LIKE '%" + dto.getDescripcion() + "%'";
	        }
	        if (dto.getUnidad() != null) {
	            sql += " AND Unidad = '" + dto.getUnidad() + "'";
	        }
	        if (dto.getCosto() != null) {
	            sql += " AND Costo = " + dto.getCosto() + "";
	        }
	        if (dto.getVenta() != null) {
	            sql += " AND Venta = " + dto.getVenta() + "";
	        }
	        if (dto.getEan() != null) {
	            sql += " AND EAN LIKE '%" + dto.getEan() + "%'";
	        }
	        if (dto.getSku() != null) {
	            sql += " AND SKU LIKE '%" + dto.getSku() + "%'";
	        }
	        if (dto.getTapices() != null) {
	            sql += " AND Tapices = " + (dto.getTapices() ? "true" : "false");
	        }
	        if (dto.getBorrado() != null) {
	            sql += " AND Borrado = " + (dto.getBorrado() ? "true" : "false");
	        }
	        
	        
	        SqlRowSet  totalResult = jdbcTemplate.queryForRowSet("SELECT COUNT(*) AS total " + sql);
	        int totalItems = 0;
	        if (totalResult.next()) {
	            totalItems = totalResult.getInt("total");
	        }
	        int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	  
	        sql = "Select * " + sql + " LIMIT " + limitValue + " OFFSET "+offset;
	        List<Producto> data = jdbcTemplate.query(sql,  (rs, rowNum) -> {
	        	return convert(rs);
	        });
	        
	        return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	    }

}

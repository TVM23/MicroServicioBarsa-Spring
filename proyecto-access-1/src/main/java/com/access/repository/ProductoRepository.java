package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.Producto;

@Repository
public class ProductoRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	public ProductoRepository(JdbcTemplate jdbcTemplate) {
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
	
	 public List<Producto> getProductoByCodigo(String codigo) {
		  String sql = "Select * FROM Producto WHERE Codigo = ?";
		  return jdbcTemplate.query(sql, (rs, rowNum) -> convert(rs), codigo);
	  }
	  
	  public List<Producto> getProductoDecripcionByCodigo(String codigo) {
		  String sql = "Select Descripcion FROM Producto WHERE Codigo = ?";
	       return jdbcTemplate.query(sql, (rs, rowNum) -> {
	           return convert(rs);
	       }, codigo);
	  }
	  
	  public List<Producto> getProductosList(String sqlClauses, List<Object> params, int limitValue, int offset){
			String sql = "SELECT * " +
					"FROM Producto WHERE 1=1 "+
	                sqlClauses + " LIMIT ? OFFSET ?";
			params.add(limitValue);
	        params.add(offset);
	        List<Producto> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
	            return convert(rs); // tu función de conversión
	        }, params.toArray());
	        
	        return data;
		}
		
		public int contarElementosProductos(String sqlClauses, List<Object> params) {
	        String countSql = "SELECT COUNT(*) AS total " + 
					"FROM Producto WHERE 1=1 " +
	                sqlClauses;
	        int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
	        return count;
		}
}

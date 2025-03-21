package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoxColorPaginationDTO;
import com.access.model.Producto_X_Color;

@Service
public class Prod_x_ColorService {
	
	private final JdbcTemplate jdbcTemplate;
	
	public Prod_x_ColorService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private Producto_X_Color convert(ResultSet rs) throws SQLException {
		Producto_X_Color prodXcolor = new Producto_X_Color();
		prodXcolor.setCodigo(rs.getString("Codigo"));
		prodXcolor.setDescProd(rs.getString("DescProd"));
		prodXcolor.setColorId(rs.getInt("ColorId"));
		prodXcolor.setDescColor(rs.getString("DescColor"));
		prodXcolor.setExistencia(rs.getInt("Existencia"));
		prodXcolor.setInventarioInicial(rs.getInt("InventarioInicial"));
		prodXcolor.setSueldo(rs.getDouble("Sueldo"));
		prodXcolor.setPrestaciones(rs.getDouble("Prestaciones"));
		prodXcolor.setAportaciones(rs.getDouble("Aportaciones"));
		return prodXcolor;
	}
	
	public PaginationResult<List<Producto_X_Color>> getProdColorFiltrados(ProductoxColorPaginationDTO dto) {
	    int pageValue = dto.getPage();
	    int limitValue = dto.getLimit();
	    int offset = (pageValue - 1) * limitValue;

	    // Construcción del SQL con INNER JOINs
	    StringBuilder sql = new StringBuilder(
	        "FROM Producto_X_Color pxc " +
	        " LEFT JOIN Producto p ON pxc.Codigo = p.Codigo " +
	        " LEFT JOIN Colores c ON pxc.ColorId = c.ColorId " +
	        " WHERE 1=1"
	    );

	    List<Object> params = new ArrayList<>();

	    // Aplicación de filtros dinámicos
	    if (dto.getCodigo() != null) {
	        sql.append(" AND pxc.Codigo = ?");
	        params.add(dto.getCodigo());
	    }
	    if (dto.getColorId() != null) {
	        sql.append(" AND pxc.ColorId = ?");
	        params.add(dto.getColorId());
	    }
	    if (dto.getDesProducto() != null) {
	        sql.append(" AND p.Descripcion LIKE ?");
	        params.add("%" + dto.getDesProducto() + "%");
	    }
	    if (dto.getDesColor() != null) {
	        sql.append(" AND c.Descripcion LIKE ?");
	        params.add("%" + dto.getDesColor() + "%");
	    }

	    // Consulta para contar el total de elementos
	    String countSql = "SELECT COUNT(DISTINCT pxc.Codigo, pxc.ColorId) AS total " + sql.toString();
	    int totalItems = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
	    int totalPages = (int) Math.ceil((double) totalItems / limitValue);
	    
	    // Consulta para obtener los datos paginados
	    String paginateSql = "SELECT pxc.*, p.Descripcion AS DescProd, c.Descripcion AS DescColor " +
                sql.toString() + " LIMIT ? OFFSET ?";
	    params.add(limitValue);
	    params.add(offset);

	    List<Producto_X_Color> data = jdbcTemplate.query(paginateSql, (rs, rowNum) -> convert(rs), params.toArray());

	    return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}

}

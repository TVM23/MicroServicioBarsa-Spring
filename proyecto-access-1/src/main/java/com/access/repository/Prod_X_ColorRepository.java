package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.Producto_X_Color;

@Repository
public class Prod_X_ColorRepository {

	private final JdbcTemplate jdbcTemplate;

	public Prod_X_ColorRepository(JdbcTemplate jdbcTemplate) {
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

	public List<Producto_X_Color> getDetallesProductoXColor(String codigo, Integer colorId) {
		String sql = "SELECT pxc.*, p.Descripcion AS DescProd, c.Descripcion AS DescColor "
				+ "FROM Producto_X_Color pxc " + "INNER JOIN Producto p ON pxc.Codigo = p.Codigo "
				+ "INNER JOIN Colores c ON pxc.ColorId = c.ColorId "
				+ "WHERE pxc.Codigo = ? AND pxc.ColorId = ? LIMIT 1";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, codigo, colorId);
	}

	public List<Producto_X_Color> getListadoProd_X_Color(String sqlClauses, List<Object> params, int limitValue,
			int offset) {
		String sql = "SELECT pxc.*, p.Descripcion AS DescProd, c.Descripcion AS DescColor "
				+ "FROM Producto_X_Color pxc " + " LEFT JOIN Producto p ON pxc.Codigo = p.Codigo "
				+ " LEFT JOIN Colores c ON pxc.ColorId = c.ColorId " + " WHERE 1=1 " + sqlClauses + " LIMIT ? OFFSET ?";
		params.add(limitValue);
		params.add(offset);
		List<Producto_X_Color> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs); // tu función de conversión
		}, params.toArray());

		return data;
	}

	public int contarElementosProd_X_Color(String sqlClauses, List<Object> params) {
		String countSql = "SELECT COUNT(DISTINCT pxc.Codigo, pxc.ColorId) AS total " + "FROM Producto_X_Color pxc "
				+ " LEFT JOIN Producto p ON pxc.Codigo = p.Codigo " + " LEFT JOIN Colores c ON pxc.ColorId = c.ColorId "
				+ " WHERE 1=1 " + sqlClauses;
		int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
		return count;
	}
}

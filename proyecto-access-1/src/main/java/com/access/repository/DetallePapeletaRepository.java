package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.DetallePapeleta;

@Repository
public class DetallePapeletaRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	public DetallePapeletaRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private DetallePapeleta convert(ResultSet rs) throws SQLException {
		DetallePapeleta detallePapeleta = new DetallePapeleta();
           detallePapeleta.setId(rs.getInt("Id"));
           detallePapeleta.setTipoId(rs.getString("TipoId"));
           detallePapeleta.setFolio(rs.getInt("Folio"));
           detallePapeleta.setCodigo(rs.getString("Codigo"));
           detallePapeleta.setDescripcionProducto(rs.getString("DescripcionProducto"));
           detallePapeleta.setColorId(rs.getInt("ColorId"));
           detallePapeleta.setNombreColor(rs.getString("NombreColor"));
           detallePapeleta.setCantidad(rs.getInt("Cantidad"));
           detallePapeleta.setClienteId(rs.getInt("ClienteId"));
           detallePapeleta.setNombreCliente(rs.getString("NombreCliente"));
           detallePapeleta.setSurtida(rs.getInt("Surtida"));
           detallePapeleta.setBackOrder(rs.getInt("BackOrder"));
           detallePapeleta.setObservacion(rs.getString("Observacion"));
           return detallePapeleta;
   }
	
	public List<DetallePapeleta> getDetallePapeleta(Integer folio) {
		String sql = "Select dp.*, "
				+ "p.Descripcion AS DescripcionProducto,"
				+ "c.Nombre AS NombreCliente,"
				+ "col.Descripcion AS NombreColor "
				+ "FROM Detalle_Papeleta dp "
				+ "INNER JOIN Producto p ON dp.Codigo = p.Codigo "
				+ "INNER JOIN Clientes c ON dp.ClienteId = c.ClienteId "
				+ "INNER JOIN Colores col ON dp.ColorId = col.ColorId "
				+ "WHERE Folio = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, folio);
	}
}

package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.model.DetallePapeleta;
import com.access.model.Papeleta;

@Service
public class DetallePapeletaService {
	
	private final JdbcTemplate jdbctemplate;
	
	public DetallePapeletaService(JdbcTemplate jdbctemplate) {
		this.jdbctemplate = jdbctemplate;
	}
	
	private DetallePapeleta convert(ResultSet rs) throws SQLException {
		DetallePapeleta detallePapeleta = new DetallePapeleta();
           detallePapeleta.setId(rs.getInt("Id"));
           detallePapeleta.setTipoId(rs.getString("TipoId"));
           detallePapeleta.setFolio(rs.getInt("Folio"));
           detallePapeleta.setCodigo(rs.getString("Codigo"));
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
				+ "c.Nombre AS NombreCliente,"
				+ "col.Descripcion AS NombreColor "
				+ "FROM Detalle_Papeleta dp "
				+ "INNER JOIN Clientes c ON dp.ClienteId = c.ClienteId "
				+ "INNER JOIN Colores col ON dp.ColorId = col.ColorId "
				+ "WHERE Folio = ?";
		return jdbctemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, folio);
	}
}

package com.access.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.model.Notificacion;

@Repository
public class NotificacionRepository {

	private final JdbcTemplate jdbcTemplate;

	public NotificacionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private Notificacion convert(ResultSet rs) throws SQLException {
		Notificacion notificacion = new Notificacion();
		notificacion.setId(rs.getInt("Id"));
		notificacion.setCodigo(rs.getString("Codigo"));
		notificacion.setDescripcion(rs.getString("Descripcion"));
		notificacion.setMensaje(rs.getString("Mensaje"));
		notificacion.setColor(rs.getString("ColorNotificacion"));
		notificacion.setFecha(rs.getString("Fecha"));
		notificacion.setMinimo(rs.getDouble("Min"));
		notificacion.setExistencia(rs.getDouble("Existencia"));
		return notificacion;
	}
	
	public void deleteNotificacionCodigo(String codigo) {
		jdbcTemplate.update("DELETE FROM Notificacion WHERE Codigo = ?", codigo);
	}

	public void insertNotificacion(String codigoMat, String descripcion, String mensaje, Double existencias, Double min,
			String colorNotif, Date fechaActual) {
		String sql = "INSERT INTO Notificacion (Codigo, Descripcion, Mensaje, Existencia, Min, ColorNotificacion, Fecha) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, codigoMat, descripcion, mensaje, existencias, min, colorNotif, fechaActual);
	}

	public void updateNotifExistente(String mensaje, Double existencias, String colorNotif, Double min, String codigo) {
		String sql = "UPDATE Notificacion SET Mensaje = ?, Existencia = ?, ColorNotificacion = ?, Minimo = ? "
				+ "WHERE Codigo = ?";
		jdbcTemplate.update(sql, mensaje, existencias, colorNotif, min, codigo);
	}
	
	public List<Notificacion> getListadoNotificaciones() {
		String sql = "SELECT * FROM Notificacion";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		});
	}
	
	public List<Notificacion> getNotificacionCodigo(String codigo) {
		String sql = "SELECT * FROM Notificacion WHERE Codigo = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convert(rs);
		}, codigo);
	}
}

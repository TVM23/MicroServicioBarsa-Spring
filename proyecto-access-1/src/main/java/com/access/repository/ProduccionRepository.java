package com.access.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.access.dto.produccion.DesactivarDetencionDTO;
import com.access.dto.produccion.DetencionDTO;
import com.access.dto.produccion.FinalizarTiempoDTO;
import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.dto.produccion.PausarTiempoDTO;
import com.access.dto.produccion.ReiniciarTiempoDTO;
import com.access.model.Detencion;
import com.access.model.Tiempo;

@Repository
public class ProduccionRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	public ProduccionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private Tiempo convertTiempo(ResultSet rs) throws SQLException {
		Tiempo tiempo = new Tiempo();
		tiempo.setId(rs.getInt("Id"));
		tiempo.setProcesoFolio(rs.getInt("ProcesoFolio"));
		tiempo.setEtapa(rs.getString("Etapa"));
		tiempo.setTiempo(rs.getInt("Tiempo"));
		tiempo.setFechaInicio(rs.getString("FechaInicio"));
		tiempo.setFechaFin(rs.getString("FechaFin"));
		tiempo.setIsRunning(rs.getBoolean("IsRunning"));
		tiempo.setIsFinished(rs.getBoolean("IsFinished"));
		tiempo.setUsuario(rs.getString("Usuario"));
		return tiempo;
	}

	private Detencion convertDetencion(ResultSet rs) throws SQLException {
		Detencion detencion = new Detencion();
		detencion.setId(rs.getInt("Id"));
		detencion.setFolio(rs.getInt("Folio"));
		detencion.setTiempoId(rs.getInt("TiempoId"));
		detencion.setEtapa(rs.getString("Etapa"));
		detencion.setMotivo(rs.getString("Motivo"));
		detencion.setFecha(rs.getString("Fecha"));
		detencion.setActiva(rs.getBoolean("Activa"));
		detencion.setUsuario(rs.getString("Usuario"));
		return detencion;
	}
	
	public List<Tiempo> getTiempoByFolioEtapa(Integer procesoFolio, String etapa) {
		String sql = "SELECT * FROM Tiempo WHERE ProcesoFolio = ? AND Etapa = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertTiempo(rs);
		}, procesoFolio, etapa);
	}
	
	public List<Detencion> getDetencionesByFolioEtapa(Integer procesoFolio, String etapa) {
		String sql = "SELECT * FROM Detencion WHERE Folio = ? AND Etapa = ?";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetencion(rs);
		}, procesoFolio, etapa);
	}
	
	public List<Tiempo> getTiemposByFolio(Integer procesoFolio) {
		String sql = "SELECT * FROM Tiempo WHERE ProcesoFolio = ? ORDER BY FechaInicio ASC";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertTiempo(rs);
		}, procesoFolio);
	}
	
	public List<Detencion> getUltimaDetencionActiva(Integer procesoFolio) {
		String sql = "SELECT * FROM Detencion WHERE Folio = ? AND Activa = 1 ORDER BY Fecha DESC LIMIT 1";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetencion(rs);
		}, procesoFolio);
	}
	
	public List<Detencion> getObtenerDetencionesFolio(Integer procesoFolio) {
		String sql = "SELECT * FROM Detencion WHERE Folio = ? ORDER BY Fecha DESC";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetencion(rs);
		}, procesoFolio);
	}
	
	public void createTiempo(IniciarTiempoDTO dto) {
		String sql = "INSERT INTO Tiempo (ProcesoFolio, Etapa, Tiempo, FechaInicio, "
				+ "IsRunning, IsFinished, Usuario) VALUES (?, ?, 0, ?, 1, 0, ?)";
		jdbcTemplate.update(sql, dto.getFolio(), dto.getEtapa(), dto.getFechaInicio(), dto.getNombreUsuario());
	}
	
	public void reanudarTiempo(IniciarTiempoDTO dto) {
		String sql = "UPDATE Tiempo SET IsRunning = 1, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
		jdbcTemplate.update(sql, dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
	}
	
	public void pausarTiempo(PausarTiempoDTO dto) {
		String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ?, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
		jdbcTemplate.update(sql, dto.getTiempo(), dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
	}
	
	public void reiniciarTiempo(ReiniciarTiempoDTO dto) {
		String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = 0, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
		jdbcTemplate.update(sql, "", dto.getFolio(), dto.getEtapa());
	}
	
	public void finalizarTiempo(FinalizarTiempoDTO dto, Boolean flag) {
		if(flag) {
			String sql = "INSERT INTO Tiempo (ProcesoFolio, Etapa, Tiempo, FechaInicio, FechaFin, "
					+ "IsRunning, IsFinished, Usuario) VALUES (?, ?, 0, ?, ?, 0, 1, ?)";
			jdbcTemplate.update(sql, dto.getFolio(), dto.getEtapa(), dto.getFechaFin(), dto.getFechaFin(),
					dto.getNombreUsuario());
		}else {
			String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ?, IsFinished = 1, "
					+ "FechaFin = ?, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, dto.getTiempo(), dto.getFechaFin(), dto.getNombreUsuario(), dto.getFolio(),
					dto.getEtapa());
		}
	}
	
	public void detencionTiempo(DetencionDTO dto, Integer id) {
		String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ?, Usuario = ? " + "WHERE ProcesoFolio = ? AND Etapa = ?";
		jdbcTemplate.update(sql, dto.getTiempo(), dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
		String sql2 = "INSERT INTO Detencion (Folio, TiempoId, Etapa, Motivo, Fecha, "
				+ "Activa, Usuario) VALUES (?, ?, ?, ?, ?, 1, ?)";
		jdbcTemplate.update(sql2, dto.getFolio(), id, dto.getEtapa(), dto.getMotivo(),
				dto.getFecha(), dto.getNombreUsuario());
	}
	
	public void desactivarDetencionTiempo(DesactivarDetencionDTO dto, Integer id) {
		String sql = "UPDATE Tiempo SET Usuario = ? " + "WHERE ProcesoFolio = ? AND Etapa = ?";
		jdbcTemplate.update(sql, "", dto.getFolio(), dto.getEtapa());
		String sql2 = "UPDATE Detencion SET Activa = 0 " + "WHERE Folio = ? AND Etapa = ? AND Id = ?";
		jdbcTemplate.update(sql2, dto.getFolio(), dto.getEtapa(), id);
	}
	
	public List<Tiempo> getTiemposPeriodoList(String sqlClauses, List<Object> params, int limitValue, int offset) {
		String sql = "SELECT * " + "FROM Tiempo WHERE 1=1 " + sqlClauses  + " ORDER BY FechaInicio DESC LIMIT ? OFFSET ?";
		params.add(limitValue);
		params.add(offset);
		List<Tiempo> data = jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertTiempo(rs); // tu función de conversión
		}, params.toArray());

		return data;
	}

	public int contarTiemposPeriodo(String sqlClauses, List<Object> params) {
		String countSql = "SELECT COUNT(*) AS total " + "FROM Tiempo WHERE 1=1 " + sqlClauses;
		int count = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
		return count;
	}
}

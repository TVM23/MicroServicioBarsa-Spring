package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.model.Papeleta;
import com.access.model.Proceso;
import com.access.model.Tiempo;

@Service
public class ProduccionService {
	private final JdbcTemplate jdbcTemplate;
	private final PapeletaService papeletaService;
	
	public ProduccionService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService) {
		this.jdbcTemplate = jdbcTemplate;
		this.papeletaService = papeletaService;
	}
	
	private Proceso convertProceso(ResultSet rs) throws SQLException {
		Proceso proceso = new Proceso();
			proceso.setTipoId(rs.getString("TipoId"));
			proceso.setFolio(rs.getInt("Folio"));
			proceso.setFecha(rs.getLong("Fecha"));
			proceso.setStatus(rs.getString("Status"));
			return proceso;
   }
	
	private Tiempo convertTiempo(ResultSet rs) throws SQLException {
		Tiempo tiempo = new Tiempo();
			tiempo.setId(rs.getInt("Id"));
			tiempo.setProcesoFolio(rs.getInt("ProcesoFolio"));
			tiempo.setEtapa(rs.getString("Etapa"));
			tiempo.setFechaInicio(rs.getLong("FechaInicio"));
			tiempo.setFechaFin(rs.getLong("FechaFin"));
			tiempo.setIsRunning(rs.getBoolean("IsRunning"));
			tiempo.setIsFinished(rs.getBoolean("IsFinished"));
			return tiempo;
   }
	
	public List<Proceso> getProcesoByFolio(Integer folio) {
        String sql = "SELECT * FROM Proceso where Folio = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convertProceso(rs);
        }, folio);
    }
	
	public List<Tiempo> getTiempoByFolioEtapa(Integer procesoFolio, String etapa) {
        String sql = "SELECT * FROM Tiempo where ProcesoFolio = ? and Etapa = ?";	       
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return convertTiempo(rs);
        }, procesoFolio, etapa);
    }
	
	public ResponseEntity<?> iniciarTiempo(IniciarTiempoDTO dto){
		//Checa si el proceso existe, y si no esta entonces lo crea
		List<Proceso> proceso = getProcesoByFolio(dto.getFolio());
		if(proceso.isEmpty()) {
			List<Papeleta> info = papeletaService.getPapeletasByFolio(dto.getFolio());
			String sql = "INSERT INTO Proceso (Folio, TipoId, Fecha, Status) VALUES (?, ?, ?, ?)";
	        jdbcTemplate.update(sql,
	        		dto.getFolio(),
	        		info.get(0).getTipoId(),
	        		dto.getFechaInicio(),
	        		info.get(0).getStatus()
	        		);
		} 
		//Checa si el tiempo existe, si no existe lo crea y lo inicia, si ya esta, enotnces lo reanuda
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if(tiempo.isEmpty()) {
			String sql = "INSERT INTO Tiempo (ProcesoFolio, Etapa, Tiempo, FechaInicio, FechaFin, "
					+ "IsRunning, IsFinished) VALUES (?, ?, 0, ?, 0, 1, 0)";
			jdbcTemplate.update(sql,
	        		dto.getFolio(),
	        		dto.getEtapa(),
	        		dto.getFechaInicio()
	        		);
		}else {
			String sql = "UPDATE Tiempo SET IsRunning = 1 WHERE ProcesoFolio = ? and Etapa = ?";
			jdbcTemplate.update(sql,
	        		dto.getFolio(),
	        		dto.getEtapa()
	        		);
		}
        return ResponseEntity.ok(Map.of("message", "Inicio de tiempo exitoso"));
	}
}

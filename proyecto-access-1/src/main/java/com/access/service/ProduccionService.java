package com.access.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.access.dto.produccion.DesactivarDetencionDTO;
import com.access.dto.produccion.DetencionDTO;
import com.access.dto.produccion.FinalizarTiempoDTO;
import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.dto.produccion.PausarTiempoDTO;
import com.access.dto.produccion.ReiniciarTiempoDTO;
import com.access.dto.produccion.TiempoDTO;
import com.access.model.Detencion;
import com.access.model.Papeleta;
import com.access.model.Tiempo;

@Service
public class ProduccionService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PapeletaService papeletaService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BitacoraTiempoService bitacoraTiempoService;


	public ProduccionService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService, KafkaTemplate<String, Object> kafkaTemplate, BitacoraTiempoService bitacoraTiempoService) {
		this.jdbcTemplate = jdbcTemplate;
		this.papeletaService = papeletaService;
        this.kafkaTemplate = kafkaTemplate;
        this.bitacoraTiempoService = bitacoraTiempoService;
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

	public ResponseEntity<?> iniciarTiempo(IniciarTiempoDTO dto) {
		List<Papeleta> info = papeletaService.getPapeletasByFolio(dto.getFolio()); 
		String descripcion = "";
		if (info.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna papeleta con este folio para iniciar proceso"));
		}
		// Checa si el tiempo existe, si no existe lo crea y lo inicia, si ya esta, enotnces lo reanuda
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			String sql = "INSERT INTO Tiempo (ProcesoFolio, Etapa, Tiempo, FechaInicio, "
					+ "IsRunning, IsFinished, Usuario) VALUES (?, ?, 0, ?, 1, 0, ?)";
			jdbcTemplate.update(sql, dto.getFolio(), dto.getEtapa(), dto.getFechaInicio(), dto.getNombreUsuario());
			descripcion = "INICIO DE TIEMPO";
		} else {
			String sql = "UPDATE Tiempo SET IsRunning = 1, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
			descripcion = "TIEMPO REANUDADO";
		}
		/////////// Envair a nestjs para generar notificacion
		String mensaje = "Tiempo iniciado en la etapa "+dto.getEtapa()+" para folio " + dto.getFolio() + " por el usuario "+dto.getNombreUsuario();
		notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
		////////// FIN DE NOTIF
		///INICIO DE BITACORATITMPO
		this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
		///FIN DE BITACORATIEMPO
		return ResponseEntity.ok(Map.of("message", "Tiempo iniciado con exito"));
	}

	public ResponseEntity<?> pausarTiempo(PausarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para pausar"));
		} else {
			String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ?, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, dto.getTiempo(), dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Tiempo pausado en la etapa "+dto.getEtapa()+" para folio " + dto.getFolio() + " por el usuario "+ dto.getNombreUsuario();
			String descripcion = "TIEMPO PAUSADO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, "", mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			///INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
			///FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo pausado con exito"));
		}
	}

	public ResponseEntity<?> reiniciarTiempo(ReiniciarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para reiniciar"));
		} else {
			String descripcion = "TIEMPO REINICIADO";
			String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = 0, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, "", dto.getFolio(), dto.getEtapa());
			///INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
			///FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo reiniciado con exito"));
		}
	}

	public ResponseEntity<?> finalizarTiempo(FinalizarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para finalizar"));
		} else {
			String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ?, IsFinished = 1, "
					+ "FechaFin = ?, Usuario = ? WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, dto.getTiempo(), dto.getFechaFin(), dto.getNombreUsuario(), dto.getFolio(), dto.getEtapa());
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Tiempo finalizado en la etapa "+dto.getEtapa()+" para folio " + dto.getFolio() + " por el usuario "+ dto.getNombreUsuario();
			String descripcion = "FINALIZACIÓN DE TIEMPO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			//////INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
			///FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo finalizado con exito"));
		}
	}

	public ResponseEntity<?> detencionTiempo(DetencionDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para detener"));
		} else {
			String sql = "UPDATE Tiempo SET IsRunning = 0, Tiempo = ? " + "WHERE ProcesoFolio = ? AND Etapa = ?";
			jdbcTemplate.update(sql, dto.getTiempo(), dto.getFolio(), dto.getEtapa());
			String sql2 = "INSERT INTO Detencion (Folio, TiempoId, Etapa, Motivo, Fecha, "
					+ "Activa, Usuario) VALUES (?, ?, ?, ?, ?, 1, ?)";
			jdbcTemplate.update(sql2, dto.getFolio(), tiempo.get(0).getId(), dto.getEtapa(), dto.getMotivo(),
					dto.getFecha(), dto.getNombreUsuario());
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Detención de tiempo en la etapa "+dto.getEtapa()+" para folio " + dto.getFolio() + " por el usuario "+ dto.getNombreUsuario();
			String descripcion = "DETENCION DE TIEMPO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			//////INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
			///FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo detenido con exito"));
		}
	}

	public ResponseEntity<?> desactivarDetencionTiempo(DesactivarDetencionDTO dto) {
		List<Detencion> detenciones = getDetencionesByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (detenciones.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna detencion que cumpla estos requisitos"));
		} else {
			String sql = "UPDATE Detencion SET Activa = 0 " + "WHERE Folio = ? AND Etapa = ? AND Id = ?";
			jdbcTemplate.update(sql, dto.getFolio(), dto.getEtapa(), detenciones.get(detenciones.size() - 1).getId());
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Se ha eliminado la detencion en la etapa "+dto.getEtapa()+" para folio " + dto.getFolio() + " por el usuario "+ dto.getNombreUsuario();
			String descripcion = "ACTIVACIÓN DE TIEMPO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			//////INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion, dto.getNombreUsuario());
			///FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Detencion desactivada con exito"));
		}
	}

	public List<Tiempo> obtenerTiempo(TiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		return tiempo;
	}

	public List<Detencion> obtenerDetencion(TiempoDTO dto) {
		List<Detencion> detencion = getDetencionesByFolioEtapa(dto.getFolio(), dto.getEtapa());
		return detencion;
	}

	public List<Tiempo> getTiemposByFolio(Integer procesoFolio) {
		String sql = "SELECT * FROM Tiempo WHERE ProcesoFolio = ? ORDER BY FechaInicio ASC";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertTiempo(rs);
		}, procesoFolio);
	}

	public List<Detencion> getUltimaDetencioActiva(Integer procesoFolio) {
		String sql = "SELECT * FROM Detencion WHERE Folio = ? AND Activa = 1 ORDER BY Fecha DESC LIMIT 1";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			return convertDetencion(rs);
		}, procesoFolio);
	}

	//public Map<String, Object> notificacionTiempo(String folio, String descripcion, String usuario, String mensaje) {
	public void notificacionTiempo(String folio, String descripcion, String usuario, String mensaje, String etapa) {
		Map<String, Object> notificacion = new HashMap<>();
		String fecha = LocalDate.now().toString();
		String area = "PRODUCCION";
		notificacion.put("codigo", folio);
		notificacion.put("descripcion", descripcion);
		notificacion.put("mensaje", mensaje);
		notificacion.put("fecha", fecha);
		notificacion.put("area", area);
		notificacion.put("etapa", etapa);
		try {
		    //restTemplate.postForEntity("http://user-authentication:3002/notificacion/crear", notificacion, Void.class);
	        kafkaTemplate.send("crear-notificacion", notificacion);
		} catch (Exception e) {
		    // Loggear el error pero no detener la ejecución
		    System.err.println("Error enviando notificación a NestJS: " + e.getMessage());
		}
		//return notificacion;
	}

}

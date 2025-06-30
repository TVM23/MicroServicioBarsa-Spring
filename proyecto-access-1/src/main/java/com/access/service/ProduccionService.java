package com.access.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.produccion.DesactivarDetencionDTO;
import com.access.dto.produccion.DetencionDTO;
import com.access.dto.produccion.FinalizarTiempoDTO;
import com.access.dto.produccion.IniciarTiempoDTO;
import com.access.dto.produccion.PausarTiempoDTO;
import com.access.dto.produccion.ReiniciarTiempoDTO;
import com.access.dto.produccion.TiempoDTO;
import com.access.dto.produccion.TiemposFechaDTO;
import com.access.model.Detencion;
import com.access.model.Papeleta;
import com.access.model.Tiempo;
import com.access.repository.ProduccionRepository;

@Service
public class ProduccionService {

	private final ProduccionRepository produccionRepository;
	private final PapeletaService papeletaService;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final BitacoraTiempoService bitacoraTiempoService;

	public ProduccionService(ProduccionRepository produccionRepository, PapeletaService papeletaService,
			KafkaTemplate<String, Object> kafkaTemplate, BitacoraTiempoService bitacoraTiempoService) {
		this.produccionRepository = produccionRepository;
		this.papeletaService = papeletaService;
		this.kafkaTemplate = kafkaTemplate;
		this.bitacoraTiempoService = bitacoraTiempoService;
	}

	public List<Tiempo> getTiempoByFolioEtapa(Integer procesoFolio, String etapa) {
		List<Tiempo> tiempo = produccionRepository.getTiempoByFolioEtapa(procesoFolio, etapa);
		return tiempo;
	}

	public List<Detencion> getDetencionesByFolioEtapa(Integer procesoFolio, String etapa) {
		List<Detencion> detencion = produccionRepository.getDetencionesByFolioEtapa(procesoFolio, etapa);
		return detencion;
	}

	public List<Tiempo> getTiemposByFolio(Integer procesoFolio) {
		List<Tiempo> tiempos = produccionRepository.getTiemposByFolio(procesoFolio);
		return tiempos;
	}

	public List<Detencion> getUltimaDetencioActiva(Integer procesoFolio) {
		List<Detencion> detencionActiva = produccionRepository.getUltimaDetencionActiva(procesoFolio);
		return detencionActiva;
	}

	public List<Detencion> getObtenerDetencionesFolio(Integer procesoFolio) {
		List<Detencion> detenciones = produccionRepository.getObtenerDetencionesFolio(procesoFolio);
		return detenciones;
	}
	
	public List<Tiempo> getTiemposPausados() {
		List<Tiempo> tiempos = produccionRepository.getTiemposPausados();
		return tiempos;
	}

	public List<Detencion> getDetencionesActivas() {
		List<Detencion> detencionesActiva = produccionRepository.getDetencionesActivas();
		return detencionesActiva;
	}
	
	public PaginationResult<List<Tiempo>> obtenerTiemposPeriodo(TiemposFechaDTO dto) {
		int pageValue = dto.getPage();
		int limitValue = dto.getLimit();
		int offset = (pageValue - 1) * limitValue;
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
			sql.append(" AND FechaInicio BETWEEN ? AND ?");
			params.add(dto.getFechaInicio());
			params.add(dto.getFechaFin());
		}
		// Conteo total
		int totalItems = produccionRepository.contarTiemposPeriodo(sql.toString(), params);
		// Paginación
		int totalPages = (int) Math.ceil((double) totalItems / limitValue);
		List<Tiempo> data = produccionRepository.getTiemposPeriodoList(sql.toString(), params, limitValue, offset);
		for (Tiempo tiempo : data) {
			tiempo.setDetenciones(getDetencionesByFolioEtapa(tiempo.getProcesoFolio(), tiempo.getEtapa()));
		}
		return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}

	public ResponseEntity<?> iniciarTiempo(IniciarTiempoDTO dto) {
		List<Papeleta> info = papeletaService.getPapeletasByFolio(dto.getFolio());
		String descripcion = "";
		if (info.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna papeleta con este folio para iniciar proceso"));
		}
		// Checa si el tiempo existe, si no existe lo crea y lo inicia, si ya esta,
		// enotnces lo reanuda
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			produccionRepository.createTiempo(dto);
			descripcion = "INICIO DE TIEMPO";
		} else {
			produccionRepository.reanudarTiempo(dto);
			descripcion = "TIEMPO REANUDADO";
		}
		/////////// Enviar a nestjs para generar notificacion
		String mensaje = "Tiempo iniciado en la etapa " + dto.getEtapa() + " para folio " + dto.getFolio()
				+ " por el usuario " + dto.getNombreUsuario();
		notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
		////////// FIN DE NOTIF
		/// INICIO DE BITACORATITMPO
		this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
				dto.getNombreUsuario());
		/// FIN DE BITACORATIEMPO
		return ResponseEntity.ok(Map.of("message", "Tiempo iniciado con exito"));
	}

	public ResponseEntity<?> pausarTiempo(PausarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para pausar"));
		} else {
			produccionRepository.pausarTiempo(dto);
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Tiempo pausado en la etapa " + dto.getEtapa() + " para folio " + dto.getFolio()
					+ " por el usuario " + dto.getNombreUsuario();
			String descripcion = "TIEMPO PAUSADO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, "", mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			/// INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
					dto.getNombreUsuario());
			/// FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo pausado con exito"));
		}
	}

	public ResponseEntity<?> reiniciarTiempo(ReiniciarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para reiniciar"));
		} else {
			produccionRepository.reiniciarTiempo(dto);
			String mensaje = "Tiempo reiniciado en la etapa " + dto.getEtapa() + " para folio " + dto.getFolio();
			String descripcion = "TIEMPO REINICIADO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			/// INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
					dto.getNombreUsuario());
			/// FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo reiniciado con exito"));
		}
	}

	public ResponseEntity<?> finalizarTiempo(FinalizarTiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			produccionRepository.finalizarTiempo(dto, true);
		} else {
			produccionRepository.finalizarTiempo(dto, false);
		}
		/////////// Envair a nestjs para generar notificacion
		String mensaje = "Tiempo finalizado en la etapa " + dto.getEtapa() + " para folio " + dto.getFolio()
				+ " por el usuario " + dto.getNombreUsuario();
		String descripcion = "FINALIZACIÓN DE TIEMPO";
		notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
		////////// FIN DE NOTIF
		////// INICIO DE BITACORATITMPO
		this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
				dto.getNombreUsuario());
		/// FIN DE BITACORATIEMPO
		return ResponseEntity.ok(Map.of("message", "Tiempo finalizado con exito"));
	}

	public ResponseEntity<?> detencionTiempo(DetencionDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (tiempo.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe un tiempo que cumpla estos requisitos para detener"));
		} else {
			List<Detencion> detencionActiva = produccionRepository.getUltimaDetencionActiva(dto.getFolio());
			if(!detencionActiva.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Ya existe una detencion activa para este tiempo"));
			}
			produccionRepository.detencionTiempo(dto, tiempo.get(0).getId());
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Detención de tiempo en la etapa " + dto.getEtapa() + " para folio " + dto.getFolio()
					+ " por el usuario " + dto.getNombreUsuario();
			String descripcion = "DETENCION DE TIEMPO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			////// INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
					dto.getNombreUsuario());
			/// FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Tiempo detenido con exito"));
		}
	}

	public ResponseEntity<?> desactivarDetencionTiempo(DesactivarDetencionDTO dto) {
		List<Detencion> detenciones = getDetencionesByFolioEtapa(dto.getFolio(), dto.getEtapa());
		if (detenciones.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna detencion que cumpla estos requisitos"));
		} else {
			Integer id = detenciones.get(detenciones.size() - 1).getId();
			produccionRepository.desactivarDetencionTiempo(dto, id);
			/////////// Envair a nestjs para generar notificacion
			String mensaje = "Se ha eliminado la detencion en la etapa " + dto.getEtapa() + " para folio "
					+ dto.getFolio() + " por el usuario " + dto.getNombreUsuario();
			String descripcion = "ACTIVACIÓN DE TIEMPO";
			notificacionTiempo(dto.getFolio().toString(), descripcion, dto.getNombreUsuario(), mensaje, dto.getEtapa());
			////////// FIN DE NOTIF
			////// INICIO DE BITACORATITMPO
			this.bitacoraTiempoService.insertarRegistro(dto.getFolio(), dto.getEtapa(), descripcion,
					dto.getNombreUsuario());
			/// FIN DE BITACORATIEMPO
			return ResponseEntity.ok(Map.of("message", "Detencion desactivada con exito"));
		}
	}

	// public Map<String, Object> notificacionTiempo(String folio, String
	// descripcion, String usuario, String mensaje) {
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
			// restTemplate.postForEntity("http://user-authentication:3002/notificacion/crear",
			// notificacion, Void.class);
			kafkaTemplate.send("crear-notificacion", notificacion);
		} catch (Exception e) {
			// Loggear el error pero no detener la ejecución
			System.err.println("Error enviando notificación a NestJS: " + e.getMessage());
		}
		// return notificacion;
	}

	// Estos se podrian combinar con los de arriba para no tener un metodo que hace
	// lo de otro metodo, pero me da flojera cambiarlo
	public List<Tiempo> obtenerTiempo(TiempoDTO dto) {
		List<Tiempo> tiempo = getTiempoByFolioEtapa(dto.getFolio(), dto.getEtapa());
		return tiempo;
	}

	public List<Detencion> obtenerDetencion(TiempoDTO dto) {
		List<Detencion> detencion = getDetencionesByFolioEtapa(dto.getFolio(), dto.getEtapa());
		return detencion;
	}
}

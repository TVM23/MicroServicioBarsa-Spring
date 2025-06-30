package com.access.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.access.model.Detencion;
import com.access.model.Materia;
import com.access.model.Notificacion;
import com.access.model.Tiempo;
import com.access.repository.NotificacionRepository;

@Service
public class NotificacionService {

	private final NotificacionRepository notificacionRepository;
	private final MateriaService materiaService;
	private final ProduccionService produccionService;

	public NotificacionService(NotificacionRepository notificacionRepository, @Lazy MateriaService materiaService,
			ProduccionService produccionService) {
		this.notificacionRepository = notificacionRepository;
		this.materiaService = materiaService;
		this.produccionService = produccionService;
	}

	public List<Notificacion> getListadoNotificaciones() {
		insertNotificacionesInicio();
		List<Notificacion> notifs = notificacionRepository.getListadoNotificaciones();
		return notifs;
	}

	public void insertNotificacionesInicio() {
		List<Materia> materias = materiaService.getMateriasNoBorradas();
		for (Materia materia : materias) {
			evaluarNotificacion(materia.getCodigoMat());
		}
	}

	public List<Notificacion> getNotificacionCodigo(String codigo) {
		List<Notificacion> notif = notificacionRepository.getNotificacionCodigo(codigo);
		return notif;
	}

	public void deleteNotificacionCodigo(String codigo) {
		notificacionRepository.deleteNotificacionCodigo(codigo);
	}

	public void evaluarNotificacion(String codigo) {
		Date fechaActual = Date.valueOf(java.time.LocalDate.now());
		Materia materia = materiaService.getMateriaByCodigo(codigo).stream().findFirst().orElse(null);
		String area = "INVENTARIO";

		if (materia == null)
			return;

		Double existencias = materia.getExistencia();
		Double max = materia.getMax();
		Double min = materia.getMin();
		Double rango = max - min;

		String colorNotif = null;
		String mensaje = null;

		if (existencias <= 0) {
			colorNotif = "ROJO";
			mensaje = "URGENTE: MATERIA '" + materia.getDescripcion() + "' HA LLEGADO A UN NIVEL DE EXISTENCIAS NULO";
		} else if (existencias <= min) {
			colorNotif = "NARANJA";
			mensaje = "ALERTA: MATERIA '" + materia.getDescripcion()
					+ "' TIENE NIVEL DE EXISTENCIAS POR DEBAJO DEL MINIMO";
		} else if (existencias <= (min + (rango * 0.25))) {
			colorNotif = "AMARILLO";
			mensaje = "AVISO: MATERIA '" + materia.getDescripcion()
					+ "' ESTÁ ACERCANDOSE A SU CANTIDAD MINIMA DE INVENTARIO";
		} else {
			// Si ya no necesita notificación, eliminarla si existe
			deleteNotificacionCodigo(codigo);
			;
			return;
		}

		List<Notificacion> existentes = getNotificacionCodigo(codigo);

		if (existentes.isEmpty()) {
			notificacionRepository.insertNotificacion(materia.getCodigoMat(), materia.getDescripcion(), mensaje,
					existencias, min, colorNotif, fechaActual);
		} else {
			String colorActual = existentes.get(0).getColor();
			if (!colorActual.equals(colorNotif)) {
				notificacionRepository.updateNotifExistente(mensaje, existencias, colorNotif, min, codigo);
			}
		}
	}

	//////// Esto es lo que se usa para que se envie la lista de posibles notifs al
	//////// nestjs
	///
	///
	public List<Notificacion> evaluarYEnviarTodas() {
		List<Materia> materias = materiaService.getMateriasNoBorradas();
		List<Notificacion> notificaciones = new ArrayList<>();

		for (Materia materia : materias) {
			Notificacion dto = evaluarNotificacion(materia);
			if (dto != null) {
				notificaciones.add(dto);
			}
		}

		return notificaciones;
	}

	private Notificacion evaluarNotificacion(Materia materia) {
		double existencia = materia.getExistencia();
		double min = materia.getMin();
		double max = materia.getMax();
		double rango = max - min;

		String color = null;
		String mensaje = null;
		String area = "INVENTARIO";

		if (existencia <= 0) {
			color = "ROJO";
			mensaje = "URGENTE: MATERIA '" + materia.getDescripcion() + "' HA LLEGADO A NIVEL NULO";
		} else if (existencia <= min) {
			color = "NARANJA";
			mensaje = "ALERTA: MATERIA '" + materia.getDescripcion() + "' POR DEBAJO DEL MÍNIMO";
		} else if (existencia <= min + rango * 0.25) {
			color = "AMARILLO";
			mensaje = "AVISO: MATERIA '" + materia.getDescripcion() + "' ACERCÁNDOSE AL MÍNIMO";
		} else {
			return null;
		}

		Notificacion dto = new Notificacion();
		dto.setCodigo(materia.getCodigoMat());
		dto.setDescripcion(materia.getDescripcion());
		dto.setMensaje(mensaje);
		dto.setColor(color);
		dto.setExistencia(existencia);
		dto.setMinimo(min);
		dto.setFecha(LocalDate.now().toString());
		dto.setArea(area);
		return dto;
	}

	public List<Notificacion> evaluarTiemposPausas() {
		List<Tiempo> tiempos = produccionService.getTiemposPausados();
		List<Detencion> detenciones = produccionService.getDetencionesActivas();
		List<Notificacion> notificaciones = new ArrayList<>();

		for (Tiempo tiempo : tiempos) {
			Notificacion dto = evaluarNotificacionTiempo(tiempo);
			if (dto != null) {
				notificaciones.add(dto);
			}
		}
		
		for (Detencion detencion : detenciones) {
			Notificacion dto = evaluarNotificacionDetencion(detencion);
			if (dto != null) {
				notificaciones.add(dto);
			}
		}

		return notificaciones;
	}

	private Notificacion evaluarNotificacionTiempo(Tiempo tiempo) {

		String codigo = tiempo.getProcesoFolio().toString();
		String mensaje = "Este proceso ha estado pausado por un periodo extenso de tiempo por el usuario " + tiempo.getUsuario();
		String area = "PRODUCCION";
		String etapa = tiempo.getEtapa();
		String descripcion = "PROCESO EN PAUSA POR MUCHO TIEMPO";

		// Convertir la fecha string a LocalDate
		String fechaStr = tiempo.getFechaPausa();
		String fechaSolo = fechaStr.split(" ")[0];  // Extraer solo la fecha
        LocalDate fechaPausa = LocalDate.parse(fechaSolo);
        LocalDate hoy = LocalDate.now();
		
		// Calcular los días transcurridos
	    long diasTranscurridos = ChronoUnit.DAYS.between(fechaPausa, hoy);

		if (diasTranscurridos >= 3) {
			Notificacion dto = new Notificacion();
			dto.setCodigo(codigo);
			dto.setEtapa(etapa);
			dto.setDescripcion(descripcion);
			dto.setMensaje(mensaje);
			dto.setFecha(LocalDate.now().toString());
			dto.setArea(area);
			return dto;
		}
		
		return null;
	}
	
	private Notificacion evaluarNotificacionDetencion(Detencion detencion) {

		String codigo = detencion.getFolio().toString();
		String mensaje = "Este proceso ha estado detenido por un periodo extenso de tiempo por el usuario " + detencion.getUsuario();
		String area = "PRODUCCION";
		String etapa = detencion.getEtapa().toUpperCase();
		String descripcion = "PROCESO EN DETENCION POR MUCHO TIEMPO";

		// Convertir la fecha string a LocalDate		
		String fechaStr = detencion.getFecha();
		String fechaSolo = fechaStr.split(" ")[0];  // Extraer solo la fecha
        LocalDate fechaPausa = LocalDate.parse(fechaSolo);
        LocalDate hoy = LocalDate.now();
		
		
		// Calcular los días transcurridos
	    long diasTranscurridos = ChronoUnit.DAYS.between(fechaPausa, hoy);

		if (diasTranscurridos >= 3) {
			Notificacion dto = new Notificacion();
			dto.setCodigo(codigo);
			dto.setEtapa(etapa);
			dto.setDescripcion(descripcion);
			dto.setMensaje(mensaje);
			dto.setFecha(LocalDate.now().toString());
			dto.setArea(area);
			return dto;
		}
		
		return null;
	}
}

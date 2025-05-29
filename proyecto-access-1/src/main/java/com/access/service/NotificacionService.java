package com.access.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.access.model.Materia;
import com.access.model.Notificacion;

@Service
public class NotificacionService {
	
	 private final JdbcTemplate jdbcTemplate;
	 private final MateriaService materiaService;
	 
	 public NotificacionService(JdbcTemplate jdbcTemplate, @Lazy MateriaService materiaService) {
		 this.jdbcTemplate = jdbcTemplate;
	     this.materiaService = materiaService;
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
	 
	 public void evaluarNotificacion(String codigo) {
		    Date fechaActual = Date.valueOf(java.time.LocalDate.now());
		    Materia materia = materiaService.getMateriaByCodigo(codigo).stream().findFirst().orElse(null);

		    if (materia == null) return;

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
		        mensaje = "ALERTA: MATERIA '" + materia.getDescripcion() + "' TIENE NIVEL DE EXISTENCIAS POR DEBAJO DEL MINIMO";
		    } else if (existencias <= (min + (rango * 0.25))) {
		        colorNotif = "AMARILLO";
		        mensaje = "AVISO: MATERIA '" + materia.getDescripcion() + "' ESTÁ ACERCANDOSE A SU CANTIDAD MINIMA DE INVENTARIO";
		    } else {
		        // Si ya no necesita notificación, eliminarla si existe
		        deleteNotificacionCodigo(codigo);;
		        return;
		    }

		    List<Notificacion> existentes = getNotificacionCodigo(codigo);

		    if (existentes.isEmpty()) {
		        String sql = "INSERT INTO Notificacion (Codigo, Descripcion, Mensaje, Existencia, Min, ColorNotificacion, Fecha) "
		                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
		        jdbcTemplate.update(sql,
		            materia.getCodigoMat(),
		            materia.getDescripcion(),
		            mensaje,
		            existencias,
		            min,
		            colorNotif,
		            fechaActual
		        );
		    } else {
		        String colorActual = existentes.get(0).getColor();
		        if (!colorActual.equals(colorNotif)) {
		            String sql = "UPDATE Notificacion SET Mensaje = ?, Existencia = ?, ColorNotificacion = ?, Minimo = ? "
		                       + "WHERE Codigo = ?";
		            jdbcTemplate.update(sql,
		                mensaje,
		                existencias,
		                colorNotif,
		                min,
		                codigo
		            );
		        }
		    }
		}
	 
	 public void insertNotificacionesInicio() {
		  List<Materia> materias = materiaService.getMateriasNoBorradas();
		  for (Materia materia : materias) {
		      evaluarNotificacion(materia.getCodigoMat());
		  }
	 }
	 
	 public List<Notificacion> getListadoNotificaciones(){
		insertNotificacionesInicio();
	    String sql = "SELECT * FROM Notificacion";
	    return jdbcTemplate.query(sql, (rs, rowNum) -> {
	    	return convert(rs);
	    });		 
	 }
	 
	 
	 public List<Notificacion> getNotificacionCodigo(String codigo){
		 String sql = "SELECT * FROM Notificacion WHERE Codigo = ?";
		 return jdbcTemplate.query(sql, (rs, rowNum) -> {
		    return convert(rs);
		 }, codigo);		 
	 }
	 
	 public void deleteNotificacionCodigo(String codigo){
	     jdbcTemplate.update("DELETE FROM Notificacion WHERE Codigo = ?", codigo);
	 }
	 
	 
}

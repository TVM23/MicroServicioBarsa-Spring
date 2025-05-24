package com.access.model;

import java.util.List;

public class MovimientoMateria {
	private Integer consecutivo;
	private Integer movId;
	private String descripcionInventario;
	private String fecha;
	private Integer folio;
	private String usuario;
	private Boolean procesada;
	private String observacion;
	private List<DetalleMovimientoMateria> detalles;
	
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Integer getConsecutivo() {
		return consecutivo;
	}
	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}
	public Integer getMovId() {
		return movId;
	}
	public void setMovId(Integer movId) {
		this.movId = movId;
	}
	public String getDescripcionInventario() {
		return descripcionInventario;
	}
	public void setDescripcionInventario(String descripcionInventario) {
		this.descripcionInventario = descripcionInventario;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Boolean getProcesada() {
		return procesada;
	}
	public void setProcesada(Boolean procesada) {
		this.procesada = procesada;
	}
	public List<DetalleMovimientoMateria> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<DetalleMovimientoMateria> detalles) {
		this.detalles = detalles;
	}
	
}

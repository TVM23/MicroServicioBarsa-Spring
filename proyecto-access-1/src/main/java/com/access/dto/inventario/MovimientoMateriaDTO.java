package com.access.dto.inventario;

import java.util.List;

public class MovimientoMateriaDTO {
	private Integer movId;
	private String fecha;
	private Integer folio;
	private Boolean procesada;
	private String observacion;
	private String autoriza;
	private String usuario;
	private List<DetalleMovMateriaDTO> detalles;
	
	public Integer getMovId() {
		return movId;
	}
	public void setMovId(Integer movId) {
		this.movId = movId;
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
	public Boolean getProcesada() {
		return procesada;
	}
	public void setProcesada(Boolean procesada) {
		this.procesada = procesada;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getAutoriza() {
		return autoriza;
	}
	public void setAutoriza(String autoriza) {
		this.autoriza = autoriza;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public List<DetalleMovMateriaDTO> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<DetalleMovMateriaDTO> detalles) {
		this.detalles = detalles;
	}
	
}

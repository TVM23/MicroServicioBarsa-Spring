package com.access.model;

import java.util.List;

public class Movimientos {
	private Integer consecutivo;
	private Integer movId;
	private String fecha;
	private Integer folio;
	private String Usuario;
	private List<Detalle_Movimiento> detalles;
	
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
		return Usuario;
	}
	public void setUsuario(String usuario) {
		Usuario = usuario;
	}
	public List<Detalle_Movimiento> getDetalles() {
		return detalles;
	}
	public void setDetalles(List<Detalle_Movimiento> detalles) {
		this.detalles = detalles;
	}
	
}

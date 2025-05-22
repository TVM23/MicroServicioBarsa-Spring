package com.access.model;

public class MovimientoInventario {
	private Integer movId;
	private String descripcion;
	private Boolean aumenta;
	private Boolean borrado;
	
	public Integer getMovId() {
		return movId;
	}
	public void setMovId(Integer movId) {
		this.movId = movId;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Boolean getAumenta() {
		return aumenta;
	}
	public void setAumenta(Boolean aumenta) {
		this.aumenta = aumenta;
	}
	public Boolean getBorrado() {
		return borrado;
	}
	public void setBorrado(Boolean borrado) {
		this.borrado = borrado;
	}
	
}
